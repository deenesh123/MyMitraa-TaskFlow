import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

export type AppTheme = 'crisp' | 'glass';

interface ThemeContextType {
  theme: AppTheme;
  setTheme: (theme: AppTheme) => void;
  toggleTheme: () => void;
  isGlass: boolean;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [theme, setThemeState] = useState<AppTheme>(() => {
    const saved = localStorage.getItem('mymitraa_theme');
    return saved === 'glass' ? 'glass' : 'crisp';
  });

  const setTheme = (t: AppTheme) => {
    setThemeState(t);
    localStorage.setItem('mymitraa_theme', t);
  };

  const toggleTheme = () => {
    setThemeState(prev => {
      const next = prev === 'glass' ? 'crisp' : 'glass';
      localStorage.setItem('mymitraa_theme', next);
      return next;
    });
  };

  useEffect(() => {
    const root = document.documentElement;
    if (theme === 'glass') {
      root.classList.add('glass-theme');
      root.classList.remove('crisp-theme');
    } else {
      root.classList.add('crisp-theme');
      root.classList.remove('glass-theme');
    }
  }, [theme]);

  return (
    <ThemeContext.Provider value={{ theme, setTheme, toggleTheme, isGlass: theme === 'glass' }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useAppTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useAppTheme must be used within a ThemeProvider');
  }
  return context;
};
