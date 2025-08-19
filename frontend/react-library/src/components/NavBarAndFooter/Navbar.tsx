import { useEffect, useState } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { User } from "../Models/User";
import { ProfilePicture } from "../Utils/ProfilePicture";
import { useAuthService } from "../Utils/useAuthService";

export const Navbar = () => {
    const location = useLocation();
    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser, setLoggedInUser] = useState<User | null>(getLoggedInUser());

    useEffect(() => {
        const handleStorageChange = () => {
            setLoggedInUser(getLoggedInUser());
        };

        window.addEventListener('storage', handleStorageChange);
        window.addEventListener('userUpdated', handleStorageChange);

        return () => {
            window.removeEventListener('storage', handleStorageChange);
            window.removeEventListener('userUpdated', handleStorageChange);
        };
    }, [getLoggedInUser]);

    if (!loggedInUser) {
        return (
            <nav className="navbar navbar-expand-md main-color bg-gradient sticky-top" data-bs-theme="dark">
                <div className="container-fluid">
                    <a className="navbar-brand me-5" href="/">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" className="bi bi-coin me-2 mb-1" viewBox="0 0 16 16">
                            <path d="M5.5 9.511c.076.954.83 1.697 2.182 1.785V12h.6v-.709c1.4-.098 2.218-.846 2.218-1.932 0-.987-.626-1.496-1.745-1.76l-.473-.112V5.57c.6.068.982.396 1.074.85h1.052c-.076-.919-.864-1.638-2.126-1.716V4h-.6v.719c-1.195.117-2.01.836-2.01 1.853 0 .9.606 1.472 1.613 1.707l.397.098v2.034c-.615-.093-1.022-.43-1.114-.9zm2.177-2.166c-.59-.137-.91-.416-.91-.836 0-.47.345-.822.915-.925v1.76h-.005zm.692 1.193c.717.166 1.048.435 1.048.91 0 .542-.412.914-1.135.982V8.518z" />
                            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16" />
                            <path d="M8 13.5a5.5 5.5 0 1 1 0-11 5.5 5.5 0 0 1 0 11m0 .5A6 6 0 1 0 8 2a6 6 0 0 0 0 12" />
                        </svg>
                        <span className="fw-bold">JackPot</span>
                    </a>
                </div>
            </nav>
        );
    }


    return (
        <nav className="navbar navbar-expand-md main-color bg-gradient sticky-top" data-bs-theme="dark">
            <div className="container-fluid">
                <a className="navbar-brand me-5" href="/">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" className="bi bi-coin me-2 mb-1" viewBox="0 0 16 16">
                        <path d="M5.5 9.511c.076.954.83 1.697 2.182 1.785V12h.6v-.709c1.4-.098 2.218-.846 2.218-1.932 0-.987-.626-1.496-1.745-1.76l-.473-.112V5.57c.6.068.982.396 1.074.85h1.052c-.076-.919-.864-1.638-2.126-1.716V4h-.6v.719c-1.195.117-2.01.836-2.01 1.853 0 .9.606 1.472 1.613 1.707l.397.098v2.034c-.615-.093-1.022-.43-1.114-.9zm2.177-2.166c-.59-.137-.91-.416-.91-.836 0-.47.345-.822.915-.925v1.76h-.005zm.692 1.193c.717.166 1.048.435 1.048.91 0 .542-.412.914-1.135.982V8.518z" />
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16" />
                        <path d="M8 13.5a5.5 5.5 0 1 1 0-11 5.5 5.5 0 0 1 0 11m0 .5A6 6 0 1 0 8 2a6 6 0 0 0 0 12" />
                    </svg>
                    <span className="fw-bold">JackPot</span>
                </a>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarContent">
                    <ul className="navbar-nav nav-pills nav-fill bg-transparent flex-fill mb-md-0 gap-3">
                        <li className="nav-item">
                            <NavLink className={`nav-link rounded-5 d-flex justify-content-center align-items-center
                                ${location.pathname === '/home' ? "bg-white text-dark shadow" : ""}`} to="/home">
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-house-fill me-1" viewBox="0 0 16 16">
                                    <path d="M8.707 1.5a1 1 0 0 0-1.414 0L.646 8.146a.5.5 0 0 0 .708.708L8 2.207l6.646 6.647a.5.5 0 0 0 .708-.708L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293z" />
                                    <path d="m8 3.293 6 6V13.5a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13.5V9.293z" />
                                </svg>
                                Home
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={`nav-link rounded-5 d-flex justify-content-center align-items-center
                                ${location.pathname === '/auctions' ? "bg-white text-dark shadow" : "bg-transparent text-dark-50"}`}
                                to="/auctions">
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-megaphone-fill me-1" viewBox="0 0 16 16">
                                    <path d="M13 2.5a1.5 1.5 0 0 1 3 0v11a1.5 1.5 0 0 1-3 0zm-1 .724c-2.067.95-4.539 1.481-7 1.656v6.237a25 25 0 0 1 1.088.085c2.053.204 4.038.668 5.912 1.56zm-8 7.841V4.934c-.68.027-1.399.043-2.008.053A2.02 2.02 0 0 0 0 7v2c0 1.106.896 1.996 1.994 2.009l.496.008a64 64 0 0 1 1.51.048m1.39 1.081q.428.032.85.078l.253 1.69a1 1 0 0 1-.983 1.187h-.548a1 1 0 0 1-.916-.599l-1.314-2.48a66 66 0 0 1 1.692.064q.491.026.966.06" />
                                </svg>
                                Auctions
                            </NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={`nav-link rounded-5 d-flex justify-content-center align-items-center
                                ${location.pathname === '/users' ? "bg-white text-dark shadow" : "bg-transparent text-dark-50"}`}
                                to="/users">
                                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-people-fill me-1" viewBox="0 0 16 16">
                                    <path d="M7 14s-1 0-1-1 1-4 5-4 5 3 5 4-1 1-1 1zm4-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6m-5.784 6A2.24 2.24 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.3 6.3 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1zM4.5 8a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5" />
                                </svg>
                                Users
                            </NavLink>
                        </li>
                        <li>
                            <div className="nav-item dropdown">
                                <button className="btn nav-link py-0 px-4" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    <div className="p-0 m-0 position-relative">
                                        <ProfilePicture pictureUrl={loggedInUser.profilePicture} rounded={true} size={40} />
                                        <span className="position-absolute bg-black p-1 text-center top-100 start-100 translate-middle badge rounded-circle border border-1">
                                            <span>⮟</span>
                                        </span>
                                    </div>

                                </button>
                                <ul className="dropdown-menu dropdown-menu-end">
                                    <li><NavLink className="dropdown-item" to={`/users/${loggedInUser.id}`}>My profile</NavLink></li>
                                    <li><hr className="dropdown-divider" /></li>
                                    <li><button className="dropdown-item" onClick={handleLogOut}>Log out</button></li>
                                </ul>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
}