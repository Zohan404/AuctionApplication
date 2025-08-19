import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ListedUserModel } from "../Models/ListedUserModel";
import { ModalHeader } from "../Utils/ModalHeader";
import { NoComponent } from "../Utils/NoComponent";
import { useAuthService } from "../Utils/useAuthService";
import { UserList } from "./UserList";

export const UserListModal: React.FC<{
    type: "followers" | "followings",
    size: string
}> = (props) => {
    const navigate = useNavigate();
    const { handleLogOut } = useAuthService();
    const { userId } = useParams();
    const [users, setUsers] = useState<ListedUserModel[]>([]);

    const handleClick = async (e: React.MouseEvent) => {
        e.preventDefault();
        const token = localStorage.getItem("token");
        const response = await fetch(`http://localhost:8081/api/users/${props.type}/${userId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });

        if (response.status === 401 || response.status === 403) {
            handleLogOut();
            return Promise.reject(new Error('Authentication failed'));
        }
        if (!response.ok) {
            navigate("../");
        }

        const data = await response.json();
        setUsers(data);
    }

    const closeModalOnClick = () => {
        const modalEl = document.getElementById(`${props.type}Modal`);
        if (modalEl) {
            modalEl.classList.remove('show');
            modalEl.setAttribute('aria-hidden', 'true');
            modalEl.setAttribute('style', 'display: none');

            // Háttér eltávolítása
            const backdrop = document.querySelector('.modal-backdrop');
            if (backdrop) {
                backdrop.remove();
            }

            // Body módosítók eltávolítása
            document.body.classList.remove('modal-open');
            document.body.style.overflow = '';
            document.body.style.paddingRight = '';
        }
    }

    return (
        <>
            <button className="btn btn-link link-underline-dark p-0" data-bs-toggle="modal" data-bs-target={`#${props.type}Modal`} onClick={handleClick}>
                <div className={`${props.size} text-capitalize fw-normal text-dark m-0`}>
                    {props.type}
                </div>
            </button>

            <div className="modal fade" id={`${props.type}Modal`} aria-labelledby="modalLabel" aria-hidden="true">
                <div className="modal-dialog modal-lg">
                    <div className="modal-content">
                        <ModalHeader>{props.type}</ModalHeader>
                        <div className="modal-body overflow-y-auto p-0" style={{ maxHeight: "500px" }}>
                            {users.length === 0
                                ?
                                <NoComponent type={props.type} size={100} />
                                :
                                users.map(user => (
                                    <UserList key={user.id} user={user} shadow={false} onClick={closeModalOnClick} />
                                ))}
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}