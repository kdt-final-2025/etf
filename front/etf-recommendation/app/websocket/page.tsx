"use client";

import { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";

interface StockPriceData {
    stockCode: string;
    currentPrice: number;
    dayOverDaySign: string;
    dayOverDayChange: number;
    dayOverDayRate: number;
    accumulatedVolume: number;
}

export default function StocksPage({ searchParams }: { searchParams: { page?: string; size?: string } }) {
    // 쿼리스트링에서 페이지와 사이즈 가져오기 (예: /?page=0&size=10)
    const page = parseInt(searchParams.page || "0", 10);
    const size = parseInt(searchParams.size || "10", 10);

    const [codes, setCodes] = useState<string[]>([]);
    const [prices, setPrices] = useState<Record<string, StockPriceData>>({});
    const wsRef = useRef<InstanceType<typeof SockJS> | null>(null);
    const subscribedRef = useRef<Set<string>>(new Set());

    // 소켓 연결: 컴포넌트 마운트 시 한 번만
    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws/stocks");
        wsRef.current = socket;

        socket.onopen = () => console.log("WebSocket 연결됨");
        socket.onmessage = e => {
            const data: StockPriceData = JSON.parse(e.data);
            setPrices(prev => ({ ...prev, [data.stockCode]: data }));
        };
        socket.onclose = () => console.log("WebSocket 연결 종료");

        return () => {
            socket.close();
        };
    }, []);

    // REST 호출 및 구독 갱신: page 또는 size 변경될 때마다
    useEffect(() => {
        let cancelled = false;

        fetch(`/api/stocks?page=${page}&size=${size}`)
            .then(res => res.json())
            .then((newCodes: string[]) => {
                if (cancelled) return;

                // unsubscribe: 이전에 구독했지만, 이제 필요 없는 코드
                subscribedRef.current.forEach(code => {
                    if (!newCodes.includes(code)) {
                        wsRef.current?.send(`UNSUBSCRIBE|${code}`);
                        subscribedRef.current.delete(code);
                    }
                });

                // subscribe: 새로 추가된 코드
                newCodes.forEach(code => {
                    if (!subscribedRef.current.has(code)) {
                        wsRef.current?.send(`SUBSCRIBE|${code}`);
                        subscribedRef.current.add(code);
                    }
                });

                setCodes(newCodes);
            })
            .catch(console.error);

        return () => {
            cancelled = true;
        };
    }, [page, size]);

    return (
        <main style={{ padding: "1rem" }}>
            <h1>ETF 목록 (페이지 {page + 1})</h1>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                <tr>
                    <th style={{ border: "1px solid #ccc", padding: "0.5rem" }}>종목코드</th>
                    <th style={{ border: "1px solid #ccc", padding: "0.5rem" }}>등락률</th>
                    <th style={{ border: "1px solid #ccc", padding: "0.5rem" }}>전일대비</th>
                </tr>
                </thead>
                <tbody>
                {codes.map(code => {
                    const d = prices[code];
                    return (
                        <tr key={code}>
                            <td style={{ border: "1px solid #ccc", padding: "0.5rem" }}>{code}</td>
                            <td style={{ border: "1px solid #ccc", padding: "0.5rem" }}>
                                {d?.dayOverDayRate?.toFixed(2)}%
                            </td>
                            <td style={{ border: "1px solid #ccc", padding: "0.5rem" }}>
                                {d?.dayOverDayChange}
                            </td>
                        </tr>
                    );
                })}
                {codes.length === 0 && (
                    <tr><td colSpan={3}>종목 정보를 불러오는 중...</td></tr>
                )}
                </tbody>
            </table>
        </main>
    );
}
