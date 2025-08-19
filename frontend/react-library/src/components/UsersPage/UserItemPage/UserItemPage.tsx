import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useNavigate, useParams } from "react-router-dom";
import { ListedUserModel } from "../../Models/ListedUserModel";
import { User } from "../../Models/User";
import { FollowersTab } from "../../Utils/FollowersTab";
import { FollowersTabPlaceHolder } from "../../Utils/FollowersTabPlaceHolder";
import { nameShower } from "../../Utils/helper";
import { ModalHeader } from "../../Utils/ModalHeader";
import { ProfilePicture } from "../../Utils/ProfilePicture";
import { useAuthService } from "../../Utils/useAuthService";

export const UserItemPage = () => {
    const navigate = useNavigate();
    const { userId } = useParams();

    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const [user, setUser] = useState<ListedUserModel | null>(null);
    const [followerIds, setFollowerIds] = useState<number[]>([]);
    useEffect(() => {
        const token = localStorage.getItem("token");

        fetch(`http://localhost:8081/api/users/${userId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (res.status === 403 || res.status === 401) {
                    handleLogOut();
                    return Promise.reject(new Error('Authentication failed'));
                }
                return res.json();
            })
            .then((json) => {
                setUser(json);
                setFollowerIds(json.followers);
            })
            .catch((err: any) => {
                navigate("../../not_found");
            });
    }, [userId]);

    const handleFollow = async (e: React.FormEvent) => {
        try {
            const response = await fetch(`http://localhost:8081/api/users/follow/${userId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                }
            })

            if (response.status === 403 || response.status === 401) {
                handleLogOut();
                return Promise.reject(new Error('Authentication failed'));
            }
            if (!response.ok) {
                return;
            }

            const data = await response.json();
            setFollowerIds(data);

            alert("Changed following status!");
        } catch (err: any) {
            return;
        }
    };

    return (
        <main className="mx-4 my-3">
            {user === null || user === undefined
                ?
                <div className="p-3">
                    <div className="d-flex align-items-center">
                        <div className="rounded-circle bg-secondary placeholder me-3" style={{ width: 100, height: 100 }}></div>
                        <div className="w-50">
                            <div className="placeholder-glow">
                                <span className="placeholder col-12 display-1"></span>
                            </div>
                        </div>
                    </div>
                    <hr />
                    <div className="d-flex align-items-center justify-content-between">
                        <div className="placeholder-glow d-flex align-items-center w-100">
                            <svg xmlns="http://www.w3.org/2000/svg" width="34" height="34" fill="currentColor" className="bi bi-geo-alt-fill me-2 mt-2" viewBox="0 0 16 16">
                                <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10m0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6" />
                            </svg>
                            <span className="placeholder col-3 display-6"></span>
                        </div>
                        <div className="d-flex flex-column gap-2 align-items-center me-5">
                            <FollowersTabPlaceHolder size="h4" />
                            <div className="btn btn-success disabled placeholder col-6"></div>
                        </div>
                    </div>
                </div>
                :
                <>
                    <div className="p-3">
                        <div className="d-flex align-content-center">
                            {
                                loggedInUser?.id === user.id
                                    ?
                                    <div className="dropdown" data-bs-theme="dark" >
                                        <button type="button" className="btn p-0 border-0 bg-transparent rounded-circle w-auto me-3" data-bs-toggle="dropdown" aria-expanded="false">
                                            <ProfilePicture pictureUrl={user.profilePicture} rounded={true} size={100} />
                                        </button>
                                        <ul className="dropdown-menu main-color p-2">
                                            <li><Link className="dropdown-item border mb-1 text-white" to={`/profile/change-profile-picture`} >
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-upload me-2 mb-1" viewBox="0 0 16 16">
                                                    <path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5" />
                                                    <path d="M7.646 1.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 2.707V11.5a.5.5 0 0 1-1 0V2.707L5.354 4.854a.5.5 0 1 1-.708-.708z" />
                                                </svg>
                                                Change profile picture
                                            </Link></li>
                                            <li><button className="dropdown-item border text-white" data-bs-toggle="modal" data-bs-target="#profilePictureModal">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-image me-2 mb-1" viewBox="0 0 16 16">
                                                    <path d="M6.002 5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0" />
                                                    <path d="M2.002 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2zm12 1a1 1 0 0 1 1 1v6.5l-3.777-1.947a.5.5 0 0 0-.577.093l-3.71 3.71-2.66-1.772a.5.5 0 0 0-.63.062L1.002 12V3a1 1 0 0 1 1-1z" />
                                                </svg>
                                                Inspect profile picture
                                            </button></li>
                                        </ul>
                                    </div>
                                    :
                                    <button type="button" className="btn p-0 border-0 bg-transparent rounded-circle w-auto me-3" data-bs-toggle="modal" data-bs-target="#profilePictureModal">
                                        <ProfilePicture pictureUrl={user.profilePicture} rounded={true} size={100} />
                                    </button>
                            }
                            <h2 className="display-1 fw-bold">{nameShower(user)}</h2>
                        </div>
                        <hr />
                        <div className="d-flex align-items-center justify-content-between">
                            <div className="d-flex">
                                <svg xmlns="http://www.w3.org/2000/svg" width="34" height="34" fill="currentColor" className="bi bi-geo-alt-fill me-2 mt-2" viewBox="0 0 16 16">
                                    <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10m0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6" />
                                </svg>
                                <h1 className="display-6">{user.location}</h1>
                            </div>
                            <div className="d-flex flex-column gap-2 align-items-center me-5">
                                <FollowersTab followers={followerIds.length} followings={user.followings?.length} button={true} size="h4" />
                                {
                                    (loggedInUser && loggedInUser.id !== user.id)
                                        ?
                                        <button className={`btn ${loggedInUser.id && followerIds.includes(loggedInUser.id) ? "btn-outline-danger" : "btn-outline-success"} d-flex justify-content-center align-items-center w-50`} onClick={handleFollow}>
                                            {loggedInUser.id && followerIds.includes(loggedInUser.id)
                                                ?
                                                <>
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-person-dash-fill me-2" viewBox="0 0 16 16">
                                                        <path fillRule="evenodd" d="M11 7.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5" />
                                                        <path d="M1 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
                                                    </svg>
                                                    <span className="h5 fw-normal m-0">Unfollow</span>
                                                </>
                                                :
                                                <>
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" className="bi bi-person-plus-fill me-2" viewBox="0 0 16 16">
                                                        <path d="M1 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
                                                        <path fillRule="evenodd" d="M13.5 5a.5.5 0 0 1 .5.5V7h1.5a.5.5 0 0 1 0 1H14v1.5a.5.5 0 0 1-1 0V8h-1.5a.5.5 0 0 1 0-1H13V5.5a.5.5 0 0 1 .5-.5" />
                                                    </svg>
                                                    <span className="h5 fw-normal m-0">Follow</span>
                                                </>
                                            }
                                        </button>
                                        :
                                        <Link className="btn btn-outline-success w-50" to="/profile">
                                            Manage profile
                                        </Link>
                                }
                            </div>
                        </div>
                    </div>
                    <div className="modal fade" id="profilePictureModal" aria-labelledby="modalLabel" aria-hidden="true">
                        <div className="modal-dialog">
                            <div className="modal-content">
                                <ModalHeader>Profile picture</ModalHeader>
                                <div className="modal-body d-flex justify-content-center border border-5 p-0">
                                    <ProfilePicture pictureUrl={user.profilePicture} rounded={false} size={500} />
                                </div>
                            </div>
                        </div>
                    </div>
                </>
            }
            <div className="m-3 border border-3 rounded-3">
                <ul className="nav nav-underline nav-fill border-bottom border-3">
                    <li className="nav-item">
                        <NavLink to="auctions" className={({ isActive }) => `nav-link text-dark d-flex justify-content-center align-items-center ${isActive && "active"}`} >
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-megaphone-fill me-1" viewBox="0 0 16 16">
                                <path d="M13 2.5a1.5 1.5 0 0 1 3 0v11a1.5 1.5 0 0 1-3 0zm-1 .724c-2.067.95-4.539 1.481-7 1.656v6.237a25 25 0 0 1 1.088.085c2.053.204 4.038.668 5.912 1.56zm-8 7.841V4.934c-.68.027-1.399.043-2.008.053A2.02 2.02 0 0 0 0 7v2c0 1.106.896 1.996 1.994 2.009l.496.008a64 64 0 0 1 1.51.048m1.39 1.081q.428.032.85.078l.253 1.69a1 1 0 0 1-.983 1.187h-.548a1 1 0 0 1-.916-.599l-1.314-2.48a66 66 0 0 1 1.692.064q.491.026.966.06" />
                            </svg>
                            Auctions
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="reviews" className={({ isActive }) => `nav-link text-dark ${isActive && "active"}`}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-hearts me-1" viewBox="0 0 16 16">
                                <path fillRule="evenodd" d="M4.931.481c1.627-1.671 5.692 1.254 0 5.015-5.692-3.76-1.626-6.686 0-5.015m6.84 1.794c1.084-1.114 3.795.836 0 3.343-3.795-2.507-1.084-4.457 0-3.343M7.84 7.642c2.71-2.786 9.486 2.09 0 8.358-9.487-6.268-2.71-11.144 0-8.358" />
                            </svg>
                            Reviews
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="bids" className={({ isActive }) => `nav-link text-dark ${isActive && "active"}`}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-cash-stack me-1" viewBox="0 0 16 16">
                                <path d="M1 3a1 1 0 0 1 1-1h12a1 1 0 0 1 1 1zm7 8a2 2 0 1 0 0-4 2 2 0 0 0 0 4" />
                                <path d="M0 5a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H1a1 1 0 0 1-1-1zm3 0a2 2 0 0 1-2 2v4a2 2 0 0 1 2 2h10a2 2 0 0 1 2-2V7a2 2 0 0 1-2-2z" />
                            </svg>
                            Bids
                        </NavLink>
                    </li>
                </ul>

                <Outlet />
            </div>
        </main >
    );
}