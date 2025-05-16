'use server'
import {cookies} from "next/headers";

export async function subscribeToEtf(etfId: number) {
    const cookieStore = await cookies()
    const accessToken = cookieStore.get('accessToken')?.value
    // if (!accessToken) {
    //     throw new Error('로그인이 필요합니다')
    // }

    const res = await fetch(`http://localhost:8080/api/v1/users/etfs/${etfId}/subscription`, {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
        },
    })

    if (!res.ok) {
        const error = await res.json()
        throw new Error(error.message || '구독 실패')
    }

    return await res.json()
}
export async function unsubscribeFromEtf(etfId: number) {
    const cookieStore = await cookies()
    const accessToken = cookieStore.get('accessToken')?.value

    // 토큰이 없을 경우 에러 처리
    if (!accessToken) {
        throw new Error('로그인이 필요합니다')
    }

    const res = await fetch(`http://localhost:8080/api/v1/users/etf/${etfId}/subscription`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
        },
    })

    if (!res.ok) {
        const error = await res.json()
        throw new Error(error.message || '구독 취소 실패')
    }

    return await res.json()  // 성공한 경우 반환
}

export async function getSubscribedEtfIds(): Promise<number[]> {
    const cookieStore = await cookies()
    const accessToken = cookieStore.get('accessToken')?.value
    if (!accessToken) return []

    const res = await fetch(`http://localhost:8080/api/v1/users/etfs/subscribes`, {
        headers: { Authorization: `Bearer ${accessToken}` },
    })
    if (!res.ok) return []

    try {
        const data = await res.json()

        if (data && Array.isArray(data.subscribeResponseList)) {
            return data.subscribeResponseList.map((s: any) => s.etfId)
    } else {
        console.error('Invalid response format or empty subscribes', data)
        return [] // 형식이 잘못된 경우 빈 배열 리턴
    }
} catch (error) {
    console.error('Error parsing response', error)
    return []  // 파싱 오류가 발생한 경우 빈 배열 리턴
}
}
