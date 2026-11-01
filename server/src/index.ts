import "dotenv/config";
import Fastify from "fastify";
import cors from "@fastify/cors";
import helmet from "@fastify/helmet";
import rateLimit from "@fastify/rate-limit";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { z } from "zod";
import { pool, migrate } from "./db.js";

const secret = process.env.JWT_SECRET;
if (!secret || secret.length < 32) throw new Error("JWT_SECRET must contain at least 32 characters");

const app = Fastify({ logger: true, bodyLimit: 1_000_000 });
await app.register(helmet);
await app.register(cors, { origin: process.env.CORS_ORIGIN?.split(",") ?? false });
await app.register(rateLimit, { max: 100, timeWindow: "1 minute" });

function tokenFor(userId: string) {
  return jwt.sign({ sub: userId }, secret!, { expiresIn: "2h", issuer: "zovnilka" });
}

function authenticatedUserId(header?: string) {
  if (!header?.startsWith("Bearer ")) return null;
  try {
    const payload = jwt.verify(header.slice(7), secret!, { issuer: "zovnilka" }) as jwt.JwtPayload;
    return typeof payload.sub === "string" ? payload.sub : null;
  } catch { return null; }
}

app.get("/health", async () => ({ ok: true, service: "zovnilka-api" }));

app.post("/api/auth/register", async (request, reply) => {
  const schema = z.object({
    username: z.string().min(3).max(32).regex(/^[a-zA-Z0-9_]+$/),
    email: z.string().email().max(254),
    password: z.string().min(10).max(72),
    displayName: z.string().trim().min(1).max(64)
  });
  const parsed = schema.safeParse(request.body);
  if (!parsed.success) return reply.code(400).send({ error: "Invalid registration data" });
  const { username, email, password, displayName } = parsed.data;
  const hash = await bcrypt.hash(password, 12);
  try {
    const result = await pool.query(
      "INSERT INTO users(username,email,password_hash,display_name) VALUES($1,lower($2),$3,$4) RETURNING id,username,email,display_name,avatar_url,about",
      [username, email, hash, displayName]
    );
    const user = result.rows[0];
    return reply.code(201).send({ user, accessToken: tokenFor(user.id) });
  } catch (error: any) {
    if (error.code === "23505") return reply.code(409).send({ error: "Username or email is already used" });
    throw error;
  }
});

app.post("/api/auth/login", async (request, reply) => {
  const parsed = z.object({ login: z.string().min(1).max(254), password: z.string().min(1).max(72) }).safeParse(request.body);
  if (!parsed.success) return reply.code(400).send({ error: "Invalid login data" });
  const { login, password } = parsed.data;
  const result = await pool.query(
    "SELECT id,username,email,password_hash,display_name,avatar_url,about FROM users WHERE lower(email)=lower($1) OR lower(username)=lower($1) LIMIT 1",
    [login]
  );
  const user = result.rows[0];
  if (!user || !(await bcrypt.compare(password, user.password_hash))) {
    return reply.code(401).send({ error: "Invalid login or password" });
  }
  delete user.password_hash;
  return { user, accessToken: tokenFor(user.id) };
});

app.get("/api/me", async (request, reply) => {
  const userId = authenticatedUserId(request.headers.authorization);
  if (!userId) return reply.code(401).send({ error: "Unauthorized" });
  const result = await pool.query(
    "SELECT id,username,email,display_name,avatar_url,about FROM users WHERE id=$1",
    [userId]
  );
  if (!result.rowCount) return reply.code(404).send({ error: "User not found" });
  return { user: result.rows[0] };
});

await migrate();
const port = Number(process.env.PORT ?? 3000);
await app.listen({ port, host: "0.0.0.0" });
