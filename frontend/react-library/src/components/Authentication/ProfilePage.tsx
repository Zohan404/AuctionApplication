import { useNavigate } from "react-router-dom";
import { User } from "../Models/User";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";
import { UserDataForm } from "./UserDataForm";

export const ProfilePage = () => {
    const navigate = useNavigate();
    const { changeLoggedInUser, handleLogOut } = useAuthService();
    const { errors, handleBackendErrors, setFieldError } = useFormErrors();

    const handleUpdateProfile = async (user: User) => {
        try {
            const token = localStorage.getItem("token");

            user.password = "000000"; // placeholder, will not be changed

            const response = await fetch(`http://localhost:8081/api/users/me`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(user)
            });

            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }
            if (response.status === 401 || response.status === 403) {
                handleLogOut();
                return Promise.reject(new Error('Authentication failed'));
            }

            const data = await response.json();
            changeLoggedInUser(data);
            navigate(`/users/${data.id}`);
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    return (
        <div className="container">
            <UserDataForm method="update" onSubmit={handleUpdateProfile} errors={errors} />
        </div>
    );
}