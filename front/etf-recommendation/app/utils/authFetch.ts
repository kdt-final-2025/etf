export async function authFetch(input: RequestInfo, init?: RequestInit): Promise<Response> {
    let res = await fetch(input, { ...init, credentials: "include" });

    if (res.status === 401) {
        // accessToken 만료 → refresh 시도
        const refreshRes = await fetch("https://localhost:8443/api/v1/refresh", {
            method: "POST",
        });

        if (refreshRes.ok) {
            // 쿠키에 저장 (expires 옵션 등 필요에 따라 조정)
            document.cookie = `accessToken=${accessToken}; path=/; samesite=None; max-age=900`;
            document.cookie = `refreshToken=${refreshToken}; path=/; samesite=None; max-age=1209600`;
            // 재발급 성공 → 원래 요청 다시 시도
            res = await fetch(input, { ...init, credentials: "include" });
        } else {
            // refreshToken도 만료 → 로그인 페이지로 이동
            window.location.href = "/login";
            throw new Error("로그인이 필요합니다.");
        }
    }

    return res;
}