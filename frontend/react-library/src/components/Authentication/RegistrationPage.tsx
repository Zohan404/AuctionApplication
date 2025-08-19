import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "../Models/User";
import { useFormErrors } from "../Utils/useFormErrors";
import { UserDataForm } from "./UserDataForm";


export const RegistrationPage: React.FC = () => {
    const navigate = useNavigate();

    const { errors, setFieldError, handleBackendErrors } = useFormErrors();

    const handleSubmit = async (user: User) => {
        try {
            const response = await fetch("http://localhost:8081/api/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(user),
            });

            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }

            alert("User registered successfully!");
            navigate("/login");
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    useEffect(() => {
        // Set the background color to green for the registration page
        document.body.className = "main-color";

        // Cleanup to reset the background color when the component unmounts
        return () => {
            document.body.className = "";
        };
    }, []);

    return (
        <div className="container text-white">
            <UserDataForm method="make" onSubmit={handleSubmit} errors={errors} />
        </div>
    );
}