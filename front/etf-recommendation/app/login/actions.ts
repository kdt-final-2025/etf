import {cookies} from "next/headers";

export async function login(loginId: string, password: string) {
    const res = await fetch("http://localhost:8080/api/v1/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ loginId, password, role: "USER" }),
    })

    if (!res.ok) {
        let message = "로그인 실패"
        try {
            const errData = await res.json()
            if (errData?.message) message = errData.message
        } catch {
            message = await res.text()
        }
        throw new Error(message)
    }// 성공 시 리다이렉트
    // 성공 시 토큰을 리스폰스 바디에서 추출
    const { accessToken, refreshToken } = await res.json();
    const cookieStore = cookies();

    // 쿠키에 저장 (expires 옵션 등 필요에 따라 조정)
    cookieStore.set('accessToken', accessToken, { path: '/', sameSite: 'None', expires: 1/96 }); // 15분 = 1/96일
    cookieStore.set('refreshToken', refreshToken, { path: '/', sameSite: 'None', expires: 14 }); // 2주
}