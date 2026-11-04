import React from 'react';
import { MascotPenguin } from '../../components/mascot/MascotPenguin';
import { ArrowLeft, Clock } from 'lucide-react';

interface PlaceholderPageProps {
  title: string;
  subtitle: string;
  onNavigate: (route: string) => void;
}

export const PlaceholderPage: React.FC<PlaceholderPageProps> = ({
  title,
  subtitle,
  onNavigate
}) => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-extrabold text-[#191627] tracking-tight">{title}</h1>
          <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>
        </div>
        <button
          type="button"
          onClick={() => onNavigate('/')}
          className="inline-flex items-center gap-1.5 px-4 py-2 border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 rounded-full text-xs font-semibold shadow-2xs transition-colors"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to Dashboard</span>
        </button>
      </div>

      <div className="bg-white rounded-3xl p-12 border border-slate-200/80 shadow-xs flex flex-col items-center justify-center text-center">
        <div className="w-24 h-24 mb-4">
          <MascotPenguin pose="sidebar-peek" size={80} />
        </div>
        <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-violet-50 text-[#7B3AEC] rounded-full text-xs font-bold mb-2">
          <Clock className="w-3.5 h-3.5" />
          <span>Scheduled for Next Sprint</span>
        </div>
        <h3 className="text-base font-bold text-[#191627] mb-1">
          {title} Module
        </h3>
        <p className="text-xs text-slate-500 max-w-md mb-6 leading-relaxed">
          {subtitle}. This module is provisioned in the backend controllers and will be linked in upcoming releases.
        </p>
        <button
          type="button"
          onClick={() => onNavigate('/')}
          className="px-6 py-2.5 bg-[#191627] hover:bg-[#28243c] text-white rounded-full text-xs font-semibold shadow-sm transition-colors"
        >
          Return to Home Dashboard
        </button>
      </div>
    </div>
  );
};
