"use client";
import {useEffect, useState, useRef} from "react";

type StockPriceData = {
    stockCode: string;
    currentPrice: number;
    dayOverDaySign: string;
    dayOverDayChange: number;
    dayOverDayRate: number;
    accumulatedVolume: number;
};

export default function StocksTable({page = 0, size = 10}: { page?: number; size?: number }) {
    const [codes, setCodes] = useState<string[]>([]);
    const [prices, setPrices] = useState<Record<string, StockPriceData>>({});
    const [socketStatus, setSocketStatus] = useState<"connecting" | "connected" | "closed">("connecting");
    const wsRef = useRef<WebSocket | null>(null);
    const subscribedRef = useRef<Set<string>>(new Set());

    useEffect(() => {
        let reconnectTimeout: NodeJS.Timeout;

        const connectSocket = () => {
            if (wsRef.current) {
                try {
                    wsRef.current.close();
                    console.log("🔌 기존 소켓 연결 종료");
                } catch (e) {
                    console.error("⚠️ 이전 소켓 닫기 실패:", e);
                }
            }

            console.log("🟡 웹소켓 연결 시도 중...");
            setSocketStatus("connecting");
            const socket = new WebSocket("ws://localhost:8080/ws/stocks");
            wsRef.current = socket;

            socket.onopen = () => {
                console.log("🟢 웹소켓 연결 성공: ws://localhost:8080/ws/stocks");
                setSocketStatus("connected");

                if (codes.length > 0) {
                    console.log(`📨 기존 코드 ${codes.length}개 재구독 시도`);
                    codes.forEach(code => {
                        socket.send(`SUBSCRIBE|${code}`);
                        subscribedRef.current.add(code);
                        console.log(`↗️ SUBSCRIBE (재연결): ${code}`);
                    });
                }
            };

            socket.onmessage = (e) => {
                try {
                    const data: StockPriceData = JSON.parse(e.data);
                    console.log("📩 수신 데이터:", data);
                    setPrices(prev => ({...prev, [data.stockCode]: data}));
                } catch (err) {
                    console.warn("⚠️ JSON 파싱 실패:", e.data, err);
                }
            };

            socket.onclose = () => {
                console.log("🔴 웹소켓 연결 종료됨");
                setSocketStatus("closed");

                reconnectTimeout = setTimeout(() => {
                    console.log("🔄 웹소켓 재연결 시도 중...");
                    connectSocket();
                }, 3000);
            };
        };

        connectSocket();

        return () => {
            clearTimeout(reconnectTimeout);
            if (wsRef.current) {
                wsRef.current.close();
                console.log("🔌 컴포넌트 언마운트 시 소켓 종료");
            }
        };
    }, []);

    useEffect(() => {
        let cancelled = false;

        const updateSubscriptions = async () => {
            try {
                const url = `http://localhost:8080/api/v1/stocks?page=${page}&size=${size}`;
                console.log(`🌐 Fetch 요청: ${url}`);
                const response = await fetch(url);
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

                const newCodes: string[] = await response.json();
                if (cancelled) return;

                console.log(`📥 fetch 받은 종목코드 목록 (page ${page + 1}):`, newCodes);

                if (wsRef.current && wsRef.current.readyState === 1) {
                    // 구독 해제
                    subscribedRef.current.forEach(code => {
                        if (!newCodes.includes(code)) {
                            wsRef.current?.send(`UNSUBSCRIBE|${code}`);
                            subscribedRef.current.delete(code);
                            console.log(`❌ UNSUBSCRIBE: ${code}`);
                        }
                    });

                    // 신규 구독
                    newCodes.forEach(code => {
                        if (!subscribedRef.current.has(code)) {
                            wsRef.current?.send(`SUBSCRIBE|${code}`);
                            subscribedRef.current.add(code);
                            console.log(`✅ SUBSCRIBE: ${code}`);
                        }
                    });
                } else {
                    console.log("⚠️ WebSocket 연결되지 않아 SUBSCRIBE/UNSUBSCRIBE 생략");
                }

                setCodes(newCodes);
            } catch (error) {
                console.error("🚨 ETF 코드 fetch 실패:", error);
            }
        };

        updateSubscriptions();

        return () => {
            cancelled = true;
        };
    }, [page, size]);

    return (
        <section style={{marginTop: 20}}>
            <h2>실시간 ETF 시세 (페이지 {page + 1})</h2>
            <div style={{marginBottom: 10}}>
                상태: {
                socketStatus === "connected" ? "🟢 연결됨" :
                    socketStatus === "connecting" ? "🟡 연결 중..." :
                        "🔴 연결 끊김"
            }
            </div>
            <table style={{width: "100%", borderCollapse: "collapse"}}>
                <thead>
                <tr>
                    <th>종목코드</th>
                    <th>등락률</th>
                    <th>전일대비</th>
                </tr>
                </thead>
                <tbody>
                {codes.map(code => {
                    const d = prices[code];
                    return (
                        <tr key={code}>
                            <td>{code}</td>
                            <td>{d?.dayOverDayRate?.toFixed(2) || "-"}%</td>
                            <td>{d?.dayOverDayChange || "-"}</td>
                        </tr>
                    );
                })}
                {codes.length === 0 && (
                    <tr>
                        <td colSpan={3} style={{textAlign: "center", padding: "20px"}}>
                            데이터 로딩 중...
                        </td>
                    </tr>
                )}
                </tbody>
            </table>
        </section>
    );
}
