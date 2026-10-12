import { VIDEOS } from "../../videos/registry";
import { navigate, hashFor } from "../../routing";
import { Card, CardContent } from "../ui/card";

export default function VideoList() {
  return (
    <div className="space-y-2">
      <h2 className="mb-2 text-base font-medium text-slate-700">Videos</h2>
      {VIDEOS.map((v) => (
        <Card key={v.slug}>
          <CardContent>
            <button className="w-full text-left" onClick={() => navigate(hashFor.video(v.slug))}>
              <span className="flex items-center justify-between">
                <span className="text-base font-semibold text-slate-900">
                  <span className="mr-1.5 text-blue-500">▶</span>
                  {v.title}
                </span>
                <span aria-hidden className="text-slate-400">&rsaquo;</span>
              </span>
              {v.skills.length > 0 && (
                <span className="mt-1.5 flex flex-wrap gap-1">
                  {v.skills.map((s) => (
                    <span key={s} className="rounded-full bg-blue-50 px-2 py-0.5 text-[10px] font-medium text-blue-700">
                      {s}
                    </span>
                  ))}
                </span>
              )}
            </button>
          </CardContent>
        </Card>
      ))}
      {VIDEOS.length === 0 && (
        <p className="py-8 text-center text-sm text-slate-400">No videos yet.</p>
      )}
    </div>
  );
}
