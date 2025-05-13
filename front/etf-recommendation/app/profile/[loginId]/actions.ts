"use server"

import { cookies } from "next/headers";
import { revalidatePath } from "next/cache";

// 프로필 업데이트 서버 액션
export async function updateProfile(loginId: string, nickname: string, isLikePrivate: boolean) {
    try {
        const cookieStore = await cookies();
        const accessToken = cookieStore.get('accessToken')?.value;

        if (!accessToken) {
            return { success: false, message: "인증 토큰이 없습니다" };
        }

        const res = await fetch("http://localhost:8080/api/v1/users", {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            },
            body: JSON.stringify({
                nickName: nickname,
                isLikePrivate: isLikePrivate
            }),
        });

        if (res.ok) {
            // 경로 재검증하여 데이터 새로고침
            revalidatePath(`/profile/${loginId}`);
            return {
                success: true,
                message: "프로필이 업데이트되었습니다",
                data: await res.json()
            };
        } else {
            const errorText = await res.text();
            return { success: false, message: `업데이트 실패: ${errorText}` };
        }
    } catch (error) {
        console.error("프로필 업데이트 오류:", error);
        return { success: false, message: "서버 오류가 발생했습니다" };
    }
}

// 사용자 프로필 가져오기 서버 액션
export async function fetchUserProfile(loginId: string) {
    try {
        const cookieStore = await cookies();
        const accessToken = cookieStore.get('accessToken')?.value;

        if (!accessToken) {
            return { success: false, message: "인증 토큰이 없습니다" };
        }

        const response = await fetch(`http://localhost:8080/api/v1/users/${loginId}`, {
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            return { success: true, data };
        } else {
            return { success: false, message: `API 오류: ${response.status}` };
        }
    } catch (error) {
        console.error("프로필 조회 오류:", error);
        return { success: false, message: "서버 오류가 발생했습니다" };
    }
}


export async function updateProfileImage(formData: FormData) {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('accessToken')?.value;

    if (!accessToken) {
        return { success: false, message: "인증 토큰이 없습니다" };
    }

    const res = await fetch("http://localhost:8080/api/v1/users/image", {
        method: "PATCH",
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
        body: formData,
    });

    if (res.ok) {
        const data = await res.json();
        return { success: true, imageUrl: data.imageUrl };
    } else {
        const error = await res.text();
        return { success: false, message: error };
    }
}

export async function changePassword(
    existingPassword: string,
    newPassword: string,
    confirmNewPassword: string
) {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;

    if (!accessToken) {
        return { success: false, message: "인증 토큰이 없습니다." };
    }

    const res = await fetch(
        "http://localhost:8080/api/v1/users/me/password",
        {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${accessToken}`,
            },
            body: JSON.stringify({
                existingPassword,
                newPassword,
                confirmNewPassword,
            }),
        }
    );

    if (res.ok) {
        return { success: true };
    } else {
        const msg = await res.text();
        return { success: false, message: msg };
    }
}
