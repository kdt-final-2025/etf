"use client";

import React, { useEffect, useRef, useState } from "react";
import { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";

type StockPriceData = {
    stockCode: string;
    currentPrice: number;
    dayOverDaySign: string;
    dayOverDayChange: number;
    dayOverDayRate: number;
    accumulatedVolume: number;
};

interface StocksTableProps {
    page: number;
    size: number;
}

export default function StocksTable({ page, size }: StocksTableProps) {
    const [codes, setCodes] = useState<string[]>([]);
    const [prices, setPrices] = useState<Record<string, StockPriceData>>({});
    const clientRef = useRef<Client | null>(null);
    const subscriptionsRef = useRef<StompSubscription[]>([]);

    // 메시지 핸들러
    function onMessage(msg: IMessage) {
        const data: StockPriceData = JSON.parse(msg.body);
        setPrices(prev => ({ ...prev, [data.stockCode]: data }));
    }

    // STOMP 클라이언트 초기화 (한 번만)
    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws/stocks");
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });

        client.onConnect = () => {
            console.log("🟢 STOMP 연결됨");

            // 페이지에 있는 종목코드가 있다면 구독 진행
            codes.forEach(code => {
                subscriptionsRef.current.push(
                    client.subscribe(`/topic/stocks/${code}`, onMessage)
                );
            });
        };

        client.activate();
        clientRef.current = client;

        return () => {
            client.deactivate();
            console.log("🛑 STOMP 클라이언트 비활성화");
        };
    }, []);

    // 페이지 변경 시 종목코드 fetch 및 구독 처리
    useEffect(() => {
        // 종목코드 가져오기
        fetch(`http://localhost:8080/api/v1/stocks?page=${page}&size=${size}`)
            .then(res => res.json())
            .then((newCodes: string[]) => {
                setCodes(newCodes);

                const client = clientRef.current;
                if (client && client.connected) {
                    // 기존 구독 해제
                    subscriptionsRef.current.forEach(sub => sub.unsubscribe());
                    subscriptionsRef.current = [];

                    // 새 종목 구독
                    newCodes.forEach(code => {
                        const sub = client.subscribe(`/topic/stocks/${code}`, onMessage);
                        subscriptionsRef.current.push(sub);
                        console.log(`✅ SUBSCRIBE /topic/stocks/${code}`);
                    });
                } else {
                    console.warn("⚠️ STOMP 연결되지 않아 구독 생략");
                }
            })
            .catch(error => {
                console.error("🚨 종목코드 fetch 실패:", error);
            });

    }, [page, size]);

    return (
        <section style={{ marginTop: 20 }}>
            <h2>실시간 ETF 시세 (페이지 {page + 1})</h2>
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
                            <td>{d?.dayOverDayRate?.toFixed(2) ?? "-"}%</td>
                            <td>{d?.dayOverDayChange ?? "-"}</td>
                        </tr>
                    );
                })}
                {codes.length === 0 && (
                    <tr>
                        <td colSpan={3} style={{ textAlign: "center", padding: "20px" }}>
                            데이터 로딩 중...
                        </td>
                    </tr>
                )}
                </tbody>
            </table>
        </section>
    );
}
