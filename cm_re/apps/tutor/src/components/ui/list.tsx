import type { HTMLAttributes, ButtonHTMLAttributes } from "react";
import { cn } from "../../lib/utils";

export function List({ className, ...props }: HTMLAttributes<HTMLUListElement>) {
  return <ul className={cn("divide-y divide-slate-200 rounded-lg border border-slate-200 bg-white", className)} {...props} />;
}

export function ListItemButton({ className, ...props }: ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <li>
      <button
        className={cn(
          "flex w-full items-center justify-between px-4 py-3 text-left text-sm hover:bg-slate-50",
          className
        )}
        {...props}
      />
    </li>
  );
}
