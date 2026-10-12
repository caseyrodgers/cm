import { useEffect, useRef, useState } from "react";
import { VIDEOS } from "../../videos/registry";
import { navigate, hashFor } from "../../routing";

export default function VideoView({ slug }: { slug: string }) {
  const video = VIDEOS.find((v) => v.slug === slug);
  const headerRef = useRef<HTMLDivElement>(null);
  const [playerHeight, setPlayerHeight] = useState<number>(0);

  useEffect(() => {
    function measure() {
      if (!headerRef.current) return;
      const bottom = headerRef.current.getBoundingClientRect().bottom;
      setPlayerHeight(window.innerHeight - bottom);
    }
    measure();
    window.addEventListener("resize", measure);
    return () => window.removeEventListener("resize", measure);
  }, []);

  if (!video) {
    return (
      <div className="p-4 text-center">
        <p className="mb-2 text-slate-600">Video not found.</p>
        <button className="text-sm text-blue-600 hover:underline" onClick={() => navigate(hashFor.videos())}>
          &larr; All videos
        </button>
      </div>
    );
  }

  return (
    <div className="-mx-4 -mb-4">
      <div ref={headerRef} className="flex items-center justify-between border-b border-slate-200 px-3 py-2">
        <button className="text-sm text-blue-600 hover:underline" onClick={() => navigate(hashFor.videos())}>
          &larr; Videos
        </button>
        <span className="text-sm font-semibold text-slate-700">{video.title}</span>
        <span />
      </div>
      {playerHeight > 0 && (
        <div className="flex items-center justify-center bg-black" style={{ height: playerHeight }}>
          <video
            controls
            autoPlay
            preload="auto"
            className="max-h-full max-w-full"
            src={video.url}
          >
            <a href={video.url} target="_blank" rel="noopener noreferrer">
              Download video
            </a>
          </video>
        </div>
      )}
    </div>
  );
}
