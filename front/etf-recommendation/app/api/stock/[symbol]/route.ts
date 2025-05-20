// app/api/stock/[symbol]/route.ts
import { NextResponse } from 'next/server';
import yahooFinance from 'yahoo-finance2';

export async function GET(request: Request, { params }: { params: { symbol: string } }) {
    try {
        const quote = await yahooFinance.quote(params.symbol);
        return NextResponse.json(quote);
    } catch (err: any) {
        return NextResponse.json({ error: err.message }, { status: 500 });
    }
}
