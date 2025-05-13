"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createComment } from "./actions";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";

interface CommentFormProps {
    etfId: number;
}

export default function CommentForm({ etfId }: CommentFormProps) {
    const [content, setContent] = useState("");
    const [isLoading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);
    const router = useRouter();

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!content.trim()) {
            setErrorMsg("댓글 내용을 입력해 주세요.");
            return;
        }
        setLoading(true);
        setErrorMsg(null);

        try {
            await createComment({ etfId, content: content.trim() });
            setContent("");
            router.refresh();
        } catch (err: any) {
            console.error(err);
            setErrorMsg(err.message || "댓글 작성 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={onSubmit} className="space-y-2">
            <Textarea
                placeholder="댓글을 입력해 주세요."
                value={content}
                onChange={(e) => setContent(e.target.value)}
                disabled={isLoading}
                rows={3}
            />
            {errorMsg && <p className="text-sm text-red-600">{errorMsg}</p>}
            <Button type="submit" disabled={isLoading}>
                {isLoading ? "작성 중…" : "댓글 작성"}
            </Button>
        </form>
    );
}
