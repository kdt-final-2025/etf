'use client';

import Link from 'next/link';
import { TableRow, TableCell } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';

export type ETF = {
  id: string;
  name: string;
  ticker: string;
  theme: string;
  price: number;
  change: number;
  volume: number;
  returnRate: number;
};

interface Props {
  etfs: ETF[];
}

export default function EtfTableBody({ etfs }: Props) {
  return (
      <>
        {etfs.map((etf, index) => (
            <TableRow
                key={etf.id}
                className="cursor-pointer hover:bg-slate-50 dark:hover:bg-slate-700"
            >
              <TableCell className="font-medium text-slate-900 dark:text-slate-200">
                {index + 1}
              </TableCell>
              <TableCell>
                <Link
                    href={`/etf/${etf.id}`}
                    className="hover:underline text-blue-600 dark:text-blue-400"
                >
                  {etf.name}
                </Link>
              </TableCell>
              <TableCell className="text-slate-900 dark:text-slate-300">
                {etf.ticker}
              </TableCell>
              <TableCell>
                <Badge variant="outline">{etf.theme}</Badge>
              </TableCell>
              <TableCell className="text-right text-slate-900 dark:text-slate-300">
                {etf.price.toLocaleString()}원
              </TableCell>
              <TableCell
                  className={`text-right ${
                      etf.change >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
                  }`}
              >
                {etf.change >= 0 ? '+' : ''}
                {etf.change.toFixed(2)}%
              </TableCell>
              <TableCell className="text-right text-slate-900 dark:text-slate-300">
                {etf.volume.toLocaleString()}
              </TableCell>
              <TableCell className="text-right font-bold text-green-600 dark:text-green-400">
                +{etf.returnRate.toFixed(2)}%
              </TableCell>
            </TableRow>
        ))}
      </>
  );
}
