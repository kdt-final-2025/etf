"use server"

import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function login(loginId: string, password: string) {
    try {
        const res = await fetch("http://localhost:8080/api/v1/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                loginId: loginId,
                password: password
            }),
        });

        if (res.ok) {
            const data = await res.json();
            const cookieStore = await cookies();
            cookieStore.set({
                name: "accessToken",
                value: data.token,
                httpOnly: true,
                path: "/"
            });
            cookieStore.set("login_id", loginId, { path: "/" });

            // 로그인 성공 결과 반환
            return { success: true, message: "로그인 성공!" };
        } else {
            const error = await res.text();
            return { success: false, message: "로그인 실패: " + error };
        }
    } catch (err) {
        console.error(err);
        return { success: false, message: "서버 오류" };
    }
}
