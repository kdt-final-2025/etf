// app/etf/[id]/actions.ts
'use server'

import { cookies } from 'next/headers'

export async function createComment(formData: FormData) {
    const content = formData.get('content') as string
    const etfId = Number(formData.get('etfId'))
    const token = (await cookies()).get('accessToken')?.value

    if (!token) {
        throw new Error('로그인이 필요합니다.')
    }

    const res = await fetch(
        'http://localhost:8080/api/v1/user/comments',
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({etfId, content}),
            cache: 'no-store',
        }
    )

    if (!res.ok) {
        throw new Error('댓글 작성에 실패했습니다.')
    }
}
    export async function getCommentsByEtfId(etfId: number) {
        const cookieStore = await cookies()
        const accessToken = cookieStore.get('accessToken')?.value

        if (!accessToken) {
            console.warn('Access token not found')
            return []
        }

        const res = await fetch(`http://localhost:8080/api/v1/user/comments?etf_id=${etfId}`, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
            next: { tags: ['comments'] },
        })


        if (!res.ok) {
            console.error('Failed to fetch comments', await res.text())
            return []
        }

        const data = await res.json()
        console.log(data)
        return data.commentResponses
    }

export async function updateComment(commentId, updatedData) {
    // 쿠키에서 accessToken 가져오기
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('accessToken')?.value;

    // accessToken이 없으면 에러 처리
    if (!accessToken) {
        throw new Error('Access token이 없습니다.');
    }

    try {
        // 댓글 수정 API 호출
        const response = await fetch(`http://localhost:8080/api/v1/user/comments/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${accessToken}`,
            },
            body: JSON.stringify(updatedData),
        });

        // 응답 상태 확인
        if (!response.ok) {
            throw new Error('댓글 수정에 실패했습니다.');
        }

    } catch (error) {
        console.error('댓글 수정 중 오류 발생:', error);
        throw error; // 에러를 호출한 곳에서 처리할 수 있도록 throw
    }
}
