import { NavLink } from 'react-router-dom';
import { navLinks } from '../../config/navigation';

function Navbar() {
  return (
    <header className="border-b border-slate-800 bg-slate-950/90 backdrop-blur">
      <nav className="mx-auto flex max-w-6xl items-center justify-between px-4 py-5">
        <div className="text-lg font-semibold text-white">
          Vera Demo Frontend
        </div>
        <div className="flex items-center gap-2 text-sm font-semibold">
          {navLinks.map((link) => (
            <NavLink
              key={link.path}
              to={link.path}
              className={({ isActive }) =>
                [
                  'rounded-full px-4 py-2 transition',
                  isActive
                    ? 'bg-brand-500/90 text-white shadow'
                    : 'text-slate-300 hover:bg-slate-800/80'
                ].join(' ')
              }
            >
              {link.label}
            </NavLink>
          ))}
        </div>
      </nav>
    </header>
  );
}

export default Navbar;