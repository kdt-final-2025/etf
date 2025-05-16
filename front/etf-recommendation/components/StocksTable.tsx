"use client";
import { useEffect, useState, useRef } from "react";
// import SockJS from "sockjs-client";

type StockPriceData = {
    stockCode: string;
    currentPrice: number;
    dayOverDaySign: string;
    dayOverDayChange: number;
    dayOverDayRate: number;
    accumulatedVolume: number;
};

export default function StocksTable(
    { page = 0, size = 10 }
    : { page?: number; size?: number }) {
    const [codes, setCodes] = useState<string[]>([]);
    const [prices, setPrices] = useState<Record<string, StockPriceData>>({});
    const [socketStatus, setSocketStatus] = useState<"connecting" | "connected" | "closed">("connecting");
    // const wsRef = useRef<InstanceType<typeof SockJS> | null>(null);
    const wsRef = useRef<WebSocket | null>(null);
    const subscribedRef = useRef<Set<string>>(new Set());

    // 1) 컴포넌트 마운트 시 소켓 연결 및 재연결 로직
    useEffect(() => {
        let reconnectTimeout: NodeJS.Timeout;

        const connectSocket = () => {
            if (wsRef.current) {
                try {
                    wsRef.current.close();
                } catch (e) {
                    console.error("이전 소켓 닫기 실패:", e);
                }
            }

            setSocketStatus("connecting");
            const socket = new WebSocket("http://localhost:8080/ws/stocks");
            wsRef.current = socket;

            socket.onopen = () => {
                console.log("🟢 SockJS 연결 성공");
                setSocketStatus("connected");

                // 연결이 되면 기존 구독 목록 다시 구독
                if (codes.length > 0) {
                    codes.forEach(code => {
                        socket.send(`SUBSCRIBE|${code}`);
                        subscribedRef.current.add(code);
                        console.log("SUBSCRIBE|", code);
                    });
                }
            };

            socket.onmessage = (e) => {
                try {
                    const data: StockPriceData = JSON.parse(e.data);
                    setPrices(prev => ({ ...prev, [data.stockCode]: data }));
                } catch (err) {
                    console.warn("JSON 파싱 실패", err);
                }
            };

            socket.onclose = () => {
                console.log("🔴 웹소켓 연결 종료");
                setSocketStatus("closed");

                // 재연결 시도
                reconnectTimeout = setTimeout(() => {
                    console.log("🔄 웹소켓 재연결 시도...");
                    connectSocket();
                }, 3000);
            };
        };

        connectSocket();

        return () => {
            clearTimeout(reconnectTimeout);
            if (wsRef.current) {
                wsRef.current.close();
            }
        };
    }, []);

    useEffect(() => {
        let cancelled = false;

        const updateSubscriptions = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/v1/stocks?page=${page}&size=${size}`);
                if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

                const newCodes: string[] = await response.json();
                if (cancelled) return;

                if (wsRef.current && wsRef.current.readyState === 1) {
                    subscribedRef.current.forEach(code => {
                        if (!newCodes.includes(code)) {
                            wsRef.current?.send(`UNSUBSCRIBE|${code}`);
                            subscribedRef.current.delete(code);
                            console.log("↪️ UNSUBSCRIBE|", code);
                        }
                    });

                    newCodes.forEach(code => {
                        if (!subscribedRef.current.has(code)) {
                            wsRef.current?.send(`SUBSCRIBE|${code}`);
                            subscribedRef.current.add(code);
                            console.log("↗️ SUBSCRIBE|", code);
                        }
                    });
                } else {
                    console.log("⚠️ 소켓 연결 준비되지 않음");
                }

                setCodes(newCodes);
            } catch (error) {
                console.error("ETF 코드 fetch 실패:", error);
            }
        };

        updateSubscriptions();

        return () => {
            cancelled = true;
        };
    }, [page, size]);

    // // 2) page, size가 바뀔 때마다 구독 대상 변경 처리
    // useEffect(() => {
    //     let cancelled = false;
    //
    //     const updateSubscriptions = async () => {
    //         try {
    //             const response = await fetch(`http://localhost:8080/api/v1/stocks?page=${page}&size=${size}`);
    //             if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
    //
    //             const newCodes: string[] = await response.json();
    //             if (cancelled) return;
    //
    //             // 소켓 연결 상태 확인
    //             if (wsRef.current && wsRef.current.readyState === 1) {
    //                 // 구독 취소할 것들
    //                 subscribedRef.current.forEach(code => {
    //                     if (!newCodes.includes(code)) {
    //                         wsRef.current?.send(`UNSUBSCRIBE|${code}`);
    //                         subscribedRef.current.delete(code);
    //                         console.log("↪️ UNSUBSCRIBE|", code);
    //                     }
    //                 });
    //
    //                 // 새로 구독할 것들
    //                 newCodes.forEach(code => {
    //                     if (!subscribedRef.current.has(code)) {
    //                         wsRef.current?.send(`SUBSCRIBE|${code}`);
    //                         subscribedRef.current.add(code);
    //                         console.log("↗️ SUBSCRIBE|", code);
    //                     }
    //                 });
    //             } else {
    //                 console.log("⚠️ 소켓 연결이 준비되지 않았습니다. 코드만 업데이트합니다.");
    //             }
    //
    //             setCodes(newCodes);
    //         } catch (error) {
    //             console.error("ETF 코드 가져오기 실패:", error);
    //         }
    //     };
    //
    //     updateSubscriptions();
    //
    //     return () => {
    //         cancelled = true;
    //     };
    // }, [page, size]);


    return (
        <section style={{ marginTop: 20 }}>
            <h2>실시간 ETF 시세 (페이지 {page + 1})</h2>
            <div style={{ marginBottom: 10 }}>
                상태: {
                socketStatus === "connected" ? "🟢 연결됨" :
                    socketStatus === "connecting" ? "🟡 연결 중..." :
                        "🔴 연결 끊김"
            }
            </div>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
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
