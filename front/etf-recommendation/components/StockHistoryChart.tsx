'use client';

import React, { useState, useEffect, useRef } from 'react';
import type { FC } from 'react';
import Chart from 'chart.js/auto';
import 'chartjs-adapter-date-fns';

type RangeKey = '7d' | '30d' | '3mo' | '6mo' | '1y';

interface RangeOption {
    key: RangeKey;
    label: string;
}

interface HistoryData {
    date: string;
    close: number;
}

const ranges: RangeOption[] = [
    { key: '7d', label: '7일' },
    { key: '30d', label: '30일' },
    { key: '3mo', label: '3개월' },
    { key: '6mo', label: '6개월' },
    { key: '1y', label: '1년' },
];

const fetchHistory = async (sym: string, rng: RangeKey): Promise<HistoryData[]> => {
    const resp = await fetch(`/api/history/${sym}?range=${rng}`);
    if (!resp.ok) {
        throw new Error(await resp.text());
    }
    return resp.json();
};

const StockHistoryChart: FC = () => {
    const [symbol, setSymbol] = useState<string>('069500.KS');
    const [range, setRange] = useState<RangeKey>('30d');
    const chartRef = useRef<HTMLCanvasElement | null>(null);
    const chartInstance = useRef<Chart | null>(null);

    useEffect(() => {
        const updateChart = async () => {
            if (!chartRef.current) return;

            try {
                const data = await fetchHistory(symbol.trim().toUpperCase(), range);
                const labels = data.map(d => d.date);
                const closes = data.map(d => d.close);

                const ctx = chartRef.current.getContext('2d');
                if (!ctx) return;

                const grad = ctx.createLinearGradient(0, 0, 0, 300);
                grad.addColorStop(0, 'rgba(58, 123, 213, 1)');
                grad.addColorStop(1, 'rgba(0, 210, 255, 0.3)');

                if (chartInstance.current) {
                    chartInstance.current.destroy();
                }

                chartInstance.current = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels,
                        datasets: [{
                            label: `${symbol} (${range})`,
                            data: closes,
                            borderColor: grad,
                            borderWidth: 2,
                            pointRadius: 0,
                            fill: false,
                            tension: 0.4,
                        }],
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                mode: 'index',
                                intersect: false,
                                backgroundColor: '#1F1F28',
                                titleColor: '#E1E1E6',
                                bodyColor: '#E1E1E6',
                                padding: 8,
                                borderColor: '#3A7BD5',
                                borderWidth: 1,
                            },
                        },
                        scales: {
                            x: {
                                type: 'time',
                                time: {
                                    parser: 'yyyy-MM-dd',
                                    unit: 'day',
                                    displayFormats: { day: 'MM-dd' },
                                },
                                grid: { display: false },
                                ticks: { color: '#888', autoSkip: true, maxTicksLimit: 5 },
                            },
                            y: {
                                grid: { color: 'rgba(100, 100, 110, 0.3)' },
                                ticks: { color: '#888' },
                                beginAtZero: false,
                            },
                        },
                        interaction: { mode: 'index', intersect: false },
                    },
                });
            } catch (err) {
                console.error(err);
            }
        };

        updateChart();
        return () => {
            chartInstance.current?.destroy();
        };
    }, [symbol, range]);

    return (
        <div
            className="mx-auto p-4 w-full max-w-[800px]"
            style={{
                backgroundColor: '#0A0A11',
                color: '#E1E1E6',
                fontFamily: '"Apple SD Gothic Neo", sans-serif',
            }}
        >
            <div className="flex justify-between items-center w-full mb-4">
                <div className="flex items-center gap-2">
                    <label className="whitespace-nowrap">심볼:</label>
                    <input
                        type="text"
                        value={symbol}
                        onChange={e => setSymbol(e.target.value)}
                        onKeyUp={e => e.key === 'Enter' && setSymbol(e.currentTarget.value)}
                        className="px-2 py-1 w-32 bg-gray-700 rounded text-gray-200 focus:outline-none"
                    />
                </div>
                <div className="flex gap-4">
                    {ranges.map(r => (
                        <button
                            key={r.key}
                            onClick={() => setRange(r.key)}
                            className={`px-4 py-2 rounded-full text-sm font-medium transition-colors duration-200 focus:outline-none ${
                                range === r.key
                                    ? 'bg-indigo-600 text-white'
                                    : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                            }`}
                        >
                            {r.label}
                        </button>
                    ))}
                </div>
            </div>

            <div className="w-full h-[300px]">
                <canvas ref={chartRef} className="w-full h-full" />
            </div>
        </div>
    );
};

export default StockHistoryChart;
