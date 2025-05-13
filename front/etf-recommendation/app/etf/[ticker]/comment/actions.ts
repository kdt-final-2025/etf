"use server";
import { cookies } from "next/headers";

export interface CommentResponse {
  id: number;
  userId: number;
  imageUrl: string;
  nickName: string;
  content: string;
  likesCount: number;
  createdAt: string; // ISO 문자열
}

export interface CommentsPageList {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  etfId: number;
  commentResponses: CommentResponse[];
}

export interface CommentCreateRequest {
  etfId: number;
  content: string;
}

export async function createComment({
  etfId,
  content,
}: CommentCreateRequest): Promise<void> {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;
  if (!accessToken) {
    throw new Error("로그인이 필요합니다.");
  }

  const res = await fetch("http://localhost:8080/api/v1/user/comments", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ etfId, content }),
  });

  if (!res.ok) {
    let msg = "댓글 작성에 실패했습니다.";
    try {
      const err = await res.json();
      if (err?.message) msg = err.message;
    } catch {}
    throw new Error(msg);
  }
}

export async function fetchCommentsPage(
  etfId: number,
  page = 0,
  size = 20,
): Promise<CommentsPageList> {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;

  const url = new URL("http://localhost:8080/api/v1/user/comments");
  url.searchParams.set("etf_id", String(etfId));
  url.searchParams.set("page", String(page));
  url.searchParams.set("size", String(size));

  const res = await fetch(url.toString(), {
    headers: accessToken
      ? { Authorization: `Bearer ${accessToken}` }
      : undefined,
  });

  if (!res.ok) {
    throw new Error("댓글 목록을 불러오는 데 실패했습니다.");
  }

  return (await res.json()) as CommentsPageList;
}
