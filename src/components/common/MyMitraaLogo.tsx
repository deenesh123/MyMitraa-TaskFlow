import React from 'react';

interface MyMitraaLogoProps {
  size?: 'sm' | 'md' | 'lg' | 'xl';
  showSubtitle?: boolean;
  variant?: 'full' | 'mark-only';
  className?: string;
  inverted?: boolean;
}

/**
 * Custom crafted logo for "MY MiTRAA"
 * Combines the dynamic folded-ribbon 'M' symbolizing friendship & collaboration ("Mitra")
 * and agile project flow across the 5 brand colours (Violet, Fuchsia, Sky Blue, Lime, Charcoal).
 */
export const MyMitraaLogo: React.FC<MyMitraaLogoProps> = ({
  size = 'md',
  showSubtitle = true,
  variant = 'full',
  className = '',
  inverted = false,
}) => {
  const dimensions = {
    sm: { icon: 28, text: 'text-sm', sub: 'text-[9px]' },
    md: { icon: 36, text: 'text-base', sub: 'text-[10px]' },
    lg: { icon: 48, text: 'text-xl', sub: 'text-xs' },
    xl: { icon: 64, text: 'text-3xl', sub: 'text-sm' },
  }[size];

  return (
    <div className={`flex items-center gap-2.5 select-none ${className}`}>
      {/* Visual Vector Mark */}
      <div
        style={{ width: dimensions.icon, height: dimensions.icon }}
        className="relative shrink-0 flex items-center justify-center filter drop-shadow-sm transition-transform hover:scale-105"
      >
        <svg
          viewBox="0 0 100 100"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          className="w-full h-full"
        >
          <defs>
            {/* Gradient 1: Left Wing (Violet to Sky Blue) */}
            <linearGradient id="mGradLeft" x1="0%" y1="100%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#7B3AEC" />
              <stop offset="100%" stopColor="#00A6FC" />
            </linearGradient>

            {/* Gradient 2: Right Wing (Fuchsia to Lime) */}
            <linearGradient id="mGradRight" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#D946EF" />
              <stop offset="100%" stopColor="#A3D801" />
            </linearGradient>

            {/* Gradient 3: Central Fold / Nexus */}
            <linearGradient id="mGradCenter" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#9333EA" />
              <stop offset="100%" stopColor="#7B3AEC" />
            </linearGradient>

            {/* Drop shadow */}
            <filter id="logoShadow" x="-10%" y="-10%" width="120%" height="130%" filterUnits="userSpaceOnUse">
              <feDropShadow dx="0" dy="4" stdDeviation="4" floodColor="#7B3AEC" floodOpacity="0.25" />
            </filter>
          </defs>

          {/* Connected Ribbon 'M' Symbolizing Collaboration & Flow */}
          <g filter="url(#logoShadow)">
            {/* Left stem & arch */}
            <path
              d="M18 82V34C18 22 28 14 40 22L50 30L34 78C32 82 24 86 18 82Z"
              fill="url(#mGradLeft)"
            />

            {/* Right stem & arch */}
            <path
              d="M82 82V34C82 22 72 14 60 22L50 30L66 78C68 82 76 86 82 82Z"
              fill="url(#mGradRight)"
            />

            {/* Center chevron interlocking both wings */}
            <path
              d="M50 32L65 52L53 78C51 81 49 81 47 78L35 52L50 32Z"
              fill="url(#mGradCenter)"
            />

            {/* Vibrant Synergy Dot (Collaboration Spark) */}
            <circle cx="50" cy="18" r="7" fill="#A3D801" stroke="#FFFFFF" strokeWidth="2" />
          </g>
        </svg>
      </div>

      {/* Typography Lockup */}
      {variant === 'full' && (
        <div className="flex flex-col text-left">
          <div className="flex items-baseline gap-1 leading-none">
            <span className={`font-black tracking-tight ${dimensions.text} ${
              inverted ? 'text-white' : 'text-[#191627]'
            }`}>
              MY
            </span>
            <span className={`font-extrabold tracking-tight ${dimensions.text} bg-gradient-to-r from-[#7B3AEC] via-[#D946EF] to-[#00A6FC] bg-clip-text text-transparent`}>
              MiTRAA
            </span>
          </div>
          {showSubtitle && (
            <span className={`font-bold tracking-widest uppercase ${dimensions.sub} ${
              inverted ? 'text-violet-200' : 'text-[#7B3AEC]'
            } mt-0.5`}>
              TaskFlow
            </span>
          )}
        </div>
      )}
    </div>
  );
};
