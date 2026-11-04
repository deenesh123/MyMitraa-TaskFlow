import React from 'react';

export type MascotPose = 'hero-clipboard' | 'sidebar-peek' | 'celebrating' | 'confused-empty' | 'waving';

interface MascotPenguinProps {
  pose?: MascotPose;
  className?: string;
  size?: number | string;
}

/**
 * High-fidelity vector penguin mascot matching the MiTRAA TaskFlow character design:
 * Cute black-and-white penguin with round violet cape, cheerful face, orange beak, and accessories.
 */
export const MascotPenguin: React.FC<MascotPenguinProps> = ({
  pose = 'hero-clipboard',
  className = '',
  size = 120
}) => {
  const width = typeof size === 'number' ? size : undefined;
  const height = typeof size === 'number' ? size : undefined;

  if (pose === 'sidebar-peek') {
    return (
      <div className={`relative flex items-center justify-center ${className}`}>
        <svg
          width={width || 72}
          height={height || 72}
          viewBox="0 0 100 100"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Sparkles */}
          <path d="M20 18L22 12L24 18L30 20L24 22L22 28L20 22L14 20L20 18Z" fill="#7B3AEC" opacity="0.6" />
          <path d="M80 25L81.5 20L83 25L88 26.5L83 28L81.5 33L80 28L75 26.5L80 25Z" fill="#D946EF" opacity="0.6" />
          
          {/* Body */}
          <ellipse cx="50" cy="55" rx="34" ry="32" fill="#191627" />
          {/* White Belly */}
          <ellipse cx="50" cy="60" rx="24" ry="22" fill="#FFFFFF" />
          {/* Cheeks */}
          <ellipse cx="36" cy="55" rx="5" ry="3.5" fill="#FDA4AF" opacity="0.8" />
          <ellipse cx="64" cy="55" rx="5" ry="3.5" fill="#FDA4AF" opacity="0.8" />
          {/* Left Eye (winking) */}
          <path d="M34 46Q40 40 44 46" stroke="#191627" strokeWidth="3" strokeLinecap="round" fill="none" />
          {/* Right Eye (sparkling open) */}
          <ellipse cx="60" cy="46" rx="5" ry="6" fill="#191627" />
          <ellipse cx="62" cy="44" rx="2" ry="2.5" fill="#FFFFFF" />
          {/* Beak */}
          <path d="M44 51C44 51 50 49 56 51C56 55 52 61 50 61C48 61 44 55 44 51Z" fill="#FB923C" />
          {/* Flipper / Hand Waving */}
          <ellipse cx="20" cy="58" rx="8" ry="12" transform="rotate(-30 20 58)" fill="#191627" />
          {/* Cape hint */}
          <path d="M30 65C38 72 62 72 70 65C66 82 34 82 30 65Z" fill="#7B3AEC" />
        </svg>
      </div>
    );
  }

  if (pose === 'celebrating') {
    return (
      <div className={`relative flex items-center justify-center ${className}`}>
        <svg
          width={width || 110}
          height={height || 110}
          viewBox="0 0 120 120"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Confetti particles */}
          <circle cx="20" cy="30" r="3" fill="#D946EF" />
          <circle cx="95" cy="25" r="3.5" fill="#A3D801" />
          <rect x="25" y="15" width="4" height="6" transform="rotate(35 25 15)" fill="#00A6FC" />
          <rect x="88" y="18" width="5" height="5" transform="rotate(-20 88 18)" fill="#FB923C" />
          <rect x="15" y="55" width="4" height="4" transform="rotate(15 15 55)" fill="#7B3AEC" />
          <circle cx="102" cy="50" r="2.5" fill="#E11D48" />

          {/* Party Cape */}
          <path d="M30 55C30 55 20 70 25 85C35 90 85 90 95 85C100 70 90 55 90 55C80 65 40 65 30 55Z" fill="#7B3AEC" />
          {/* Main Body */}
          <ellipse cx="60" cy="65" rx="32" ry="34" fill="#191627" />
          {/* White Belly */}
          <ellipse cx="60" cy="70" rx="23" ry="24" fill="#FFFFFF" />
          {/* Cheeks */}
          <ellipse cx="46" cy="64" rx="4.5" ry="3" fill="#FDA4AF" />
          <ellipse cx="74" cy="64" rx="4.5" ry="3" fill="#FDA4AF" />
          {/* Happy curved eyes */}
          <path d="M44 54C46 50 51 50 53 54" stroke="#191627" strokeWidth="3" strokeLinecap="round" />
          <path d="M67 54C69 50 74 50 76 54" stroke="#191627" strokeWidth="3" strokeLinecap="round" />
          {/* Open Joyful Beak */}
          <path d="M52 58C52 58 60 56 68 58C67 65 60 69 60 69C60 69 53 65 52 58Z" fill="#FB923C" />
          <path d="M55 60C57 65 63 65 65 60" fill="#BE123C" opacity="0.6" />
          {/* Raised Flippers in Celebration */}
          <ellipse cx="26" cy="46" rx="8" ry="16" transform="rotate(-40 26 46)" fill="#191627" />
          <ellipse cx="94" cy="46" rx="8" ry="16" transform="rotate(40 94 46)" fill="#191627" />
          {/* Feet */}
          <ellipse cx="48" cy="98" rx="8" ry="4.5" fill="#FB923C" />
          <ellipse cx="72" cy="98" rx="8" ry="4.5" fill="#FB923C" />
        </svg>
      </div>
    );
  }

  if (pose === 'confused-empty') {
    return (
      <div className={`relative flex items-center justify-center ${className}`}>
        <svg
          width={width || 90}
          height={height || 90}
          viewBox="0 0 100 100"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* Question mark float */}
          <text x="68" y="32" fontSize="22" fontWeight="bold" fill="#7B3AEC" opacity="0.85">?</text>
          {/* Body */}
          <ellipse cx="50" cy="58" rx="30" ry="30" fill="#191627" />
          {/* White Belly */}
          <ellipse cx="50" cy="63" rx="20" ry="21" fill="#FFFFFF" />
          {/* Tilted head / Puzzled eyes */}
          <ellipse cx="42" cy="48" rx="4" ry="4" fill="#191627" />
          <ellipse cx="43" cy="47" rx="1.5" ry="1.5" fill="#FFFFFF" />
          <ellipse cx="58" cy="48" rx="3" ry="3" fill="#191627" />
          {/* Beak tilted */}
          <path d="M46 54C46 54 52 52 56 55C54 59 50 61 48 60Z" fill="#FB923C" />
          {/* Scratching flipper */}
          <ellipse cx="30" cy="42" rx="7" ry="13" transform="rotate(-55 30 42)" fill="#191627" />
          <ellipse cx="74" cy="62" rx="7" ry="13" transform="rotate(15 74 62)" fill="#191627" />
          {/* Feet */}
          <ellipse cx="40" cy="88" rx="7" ry="3.5" fill="#FB923C" />
          <ellipse cx="60" cy="88" rx="7" ry="3.5" fill="#FB923C" />
        </svg>
      </div>
    );
  }

  // Default: hero-clipboard (Welcome banner hero)
  return (
    <div className={`relative flex items-center justify-center select-none ${className}`}>
      <svg
        width={width || 180}
        height={height || 180}
        viewBox="0 0 180 180"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <radialGradient id="heroGlow" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stopColor="#7B3AEC" stopOpacity="0.18" />
            <stop offset="100%" stopColor="#7B3AEC" stopOpacity="0" />
          </radialGradient>
          <linearGradient id="capeGrad" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stopColor="#8B5CF6" />
            <stop offset="100%" stopColor="#6D28D9" />
          </linearGradient>
          <linearGradient id="clipboardGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="#FFFFFF" />
            <stop offset="100%" stopColor="#F1F5F9" />
          </linearGradient>
        </defs>

        {/* Ambient Glow */}
        <circle cx="90" cy="90" r="82" fill="url(#heroGlow)" />

        {/* Floating Sparkles & Arrows */}
        <path d="M42 42L44 34L46 42L54 44L46 46L44 54L42 46L34 44L42 42Z" fill="#D946EF" opacity="0.75" />
        <path d="M148 40L149.5 34L151 40L157 41.5L151 43L149.5 49L148 43L142 41.5L148 40Z" fill="#00A6FC" opacity="0.8" />
        <path d="M152 118L153.5 113L155 118L160 119.5L155 121L153.5 126L152 121L147 119.5L152 118Z" fill="#A3D801" opacity="0.8" />

        {/* Violet Superhero Cape */}
        <path
          d="M48 85 C32 105 38 140 58 148 C72 153 118 153 132 145 C146 135 145 105 132 85 C118 100 62 100 48 85 Z"
          fill="url(#capeGrad)"
        />
        {/* Cape Knot / Medal with MiTRAA 'M' */}
        <circle cx="90" cy="94" r="8" fill="#FACC15" stroke="#EAB308" strokeWidth="1.5" />
        <path d="M87 97L87 91L90 94L93 91L93 97" stroke="#854D0E" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />

        {/* Main Body */}
        <ellipse cx="90" cy="92" rx="44" ry="48" fill="#191627" />

        {/* White Belly */}
        <ellipse cx="90" cy="98" rx="31" ry="34" fill="#FFFFFF" />

        {/* Rosy Cheeks */}
        <ellipse cx="70" cy="88" rx="6.5" ry="4.5" fill="#FDA4AF" opacity="0.85" />
        <ellipse cx="110" cy="88" rx="6.5" ry="4.5" fill="#FDA4AF" opacity="0.85" />

        {/* Left Eye (wink with cheerful curve) */}
        <path d="M68 76 Q76 68 82 76" stroke="#191627" strokeWidth="3.5" strokeLinecap="round" fill="none" />

        {/* Right Eye (sparkling large pupil) */}
        <ellipse cx="102" cy="74" rx="7" ry="8.5" fill="#191627" />
        <ellipse cx="105" cy="71" rx="2.5" ry="3" fill="#FFFFFF" />
        <ellipse cx="99" cy="76" rx="1.2" ry="1.2" fill="#FFFFFF" />

        {/* Cheerful Beak */}
        <path d="M80 82 C80 82 90 79 100 82 C100 88 94 95 90 95 C86 95 80 88 80 82 Z" fill="#FB923C" />

        {/* Left Hand: Thumbs Up */}
        <path d="M46 88 C40 85 36 78 40 72 C44 66 50 72 50 78 Z" fill="#191627" />
        <ellipse cx="48" cy="96" rx="9" ry="16" transform="rotate(-30 48 96)" fill="#191627" />

        {/* Right Hand: Holding Green Checklist Clipboard */}
        <g transform="translate(112, 60)">
          {/* Clipboard Board */}
          <rect x="0" y="8" width="40" height="54" rx="6" fill="#A3D801" />
          <rect x="3" y="12" width="34" height="46" rx="4" fill="url(#clipboardGrad)" />
          {/* Clip */}
          <rect x="13" y="4" width="14" height="7" rx="2" fill="#64748B" />
          <circle cx="20" cy="7" r="1.5" fill="#CBD5E1" />
          {/* Tasks checkmarks on clipboard */}
          <path d="M8 22L12 25L17 19" stroke="#16A34A" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          <line x1="20" y1="22" x2="31" y2="22" stroke="#94A3B8" strokeWidth="2" strokeLinecap="round" />

          <path d="M8 32L12 35L17 29" stroke="#16A34A" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          <line x1="20" y1="32" x2="31" y2="32" stroke="#94A3B8" strokeWidth="2" strokeLinecap="round" />

          <path d="M8 42L12 45L17 39" stroke="#16A34A" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          <line x1="20" y1="42" x2="31" y2="42" stroke="#94A3B8" strokeWidth="2" strokeLinecap="round" />
        </g>
        {/* Right Flipper holding the board */}
        <ellipse cx="120" cy="100" rx="10" ry="15" transform="rotate(35 120 100)" fill="#191627" />

        {/* Feet */}
        <ellipse cx="74" cy="140" rx="12" ry="6.5" fill="#FB923C" />
        <ellipse cx="106" cy="140" rx="12" ry="6.5" fill="#FB923C" />
      </svg>
    </div>
  );
};
