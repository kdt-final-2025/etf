// app/etf/[id]/actions.ts
'use server'

import { cookies } from 'next/headers'

export async function createComment(formData: FormData) {
    const content = formData.get('content') as string
    const etfId   = Number(formData.get('etfId'))
    const token   = (await cookies()).get('accessToken')?.value

    if (!token) {
        throw new Error('로그인이 필요합니다.')
    }

    const res = await fetch(
        'http://localhost:8080/api/v1/user/comments',
        {
            method: 'POST',
            headers: {
                'Content-Type':  'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({ etfId, content }),
            cache: 'no-store',
        }
    )

    if (!res.ok) {
        throw new Error('댓글 작성에 실패했습니다.')
    }
}