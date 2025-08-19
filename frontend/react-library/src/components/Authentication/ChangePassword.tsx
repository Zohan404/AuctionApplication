import { useState } from "react";
import { SubmitButton } from "../Utils/SubmitButton";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";

export const ChangePassword: React.FC = () => {
    const { changeLoggedInUser, handleLogOut } = useAuthService();
    const { errors, setFieldError, handleBackendErrors, clearErrors } = useFormErrors();

    const [formData, setFormData] = useState<{
        oldPassword: string;
        newPassword: string;
        confirmPassword: string
    }>
        ({ oldPassword: "", newPassword: "", confirmPassword: "" });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();

        try {
            const token = localStorage.getItem("token");
            const response = await fetch("http://localhost:8081/api/users/me/password", {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(formData),
            });

            if (response.status === 401 || response.status === 403) {
                handleLogOut();
                return Promise.reject(new Error('Authentication failed'));
            }
            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }

            const data = await response.json();
            changeLoggedInUser(data);

            alert("Password changed successfully!");
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData(prev => ({ ...prev, [id]: value }));
    };

    return (
        <div className="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
            <h1 className="mb-5 fw-bold text-center">Change password</h1>
            <form className="w-50" onSubmit={handleSubmit}>
                <div className="form-floating mb-3">
                    <input type="password" className={`form-control ${errors.oldPassword && "is-invalid"}`}
                        id="oldPassword" placeholder="Current password" value={formData.oldPassword} onChange={handleChange} />
                    <label htmlFor="oldPassword">Current password</label>
                    {errors.oldPassword && <div className="invalid-feedback text-danger">{errors.oldPassword}</div>}
                </div>
                <div className="form-floating mb-3">
                    <input type="password" className={`form-control ${errors.newPassword && "is-invalid"}`}
                        id="newPassword" placeholder="New password" value={formData.newPassword} onChange={handleChange} />
                    <label htmlFor="newPassword">New password</label>
                    {errors.newPassword && <div className="invalid-feedback text-danger">{errors.newPassword}</div>}
                </div>
                <div className="form-floating mb-3">
                    <input type="password" className={`form-control ${errors.confirmPassword && "is-invalid"}`}
                        id="confirmPassword" placeholder="Confirm new password" value={formData.confirmPassword} onChange={handleChange} />
                    <label htmlFor="confirmPassword">Confirm new password</label>
                    {errors.confirmPassword && <div className="invalid-feedback text-danger">{errors.confirmPassword}</div>}
                </div>
                {errors.general && <p className="text-danger">{errors.general}</p>}
                <SubmitButton />
            </form>
        </div>
    );
}