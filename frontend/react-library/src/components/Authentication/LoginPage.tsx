import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { User } from "../Models/User";
import { SubmitButton } from "../Utils/SubmitButton";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";

export const LoginPage = () => {
    const navigate = useNavigate();
    const { getLoggedInUser,changeLoggedInUser } = useAuthService();
    const [loggedInUser,setLoggedInUser] = useState<User | null>(getLoggedInUser());
    const [user, setUser] = useState({ email: "", password: "" });
    const { errors, setFieldError, handleBackendErrors, clearErrors } = useFormErrors();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setUser(user => ({ ...user, [id]: value }));
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();

        try {
            const response = await fetch("http://localhost:8081/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(user),
            });

            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }
            
            const data = await response.json();
            localStorage.setItem("token", data.token);

            const responseUser = await fetch("http://localhost:8081/api/users/me", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${data.token}`
                }
            });

            if (!responseUser.ok) {
                await handleBackendErrors(responseUser);
                localStorage.removeItem("token");
                return;
            }
            const dataUser: User = await responseUser.json();
            console.log(dataUser)
            changeLoggedInUser(dataUser);
            setLoggedInUser(dataUser);
            console.log(loggedInUser);

            alert("Login successful!");
            navigate("/home");
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    useEffect(() => {
        // Set the background color to green for the login page
        document.body.className = "main-color";

        // Cleanup to reset the background color when the component unmounts
        return () => {
            document.body.className = "";
        };
    }, []);

    return (
        <main className="auth">
            <div id="liveAlerts"></div>
            <form className="position-absolute top-50 start-50 translate-middle p-5 col-9 col-md-6" onSubmit={handleLogin} noValidate >
                <h1 className="fw-medium text-white text-center mb-3">Login</h1>
                <div className="input-group mb-3">
                    <span className="input-group-text">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" className="bi bi-envelope-fill" viewBox="0 0 16 16">
                            <path d="M.05 3.555A2 2 0 0 1 2 2h12a2 2 0 0 1 1.95 1.555L8 8.414zM0 4.697v7.104l5.803-3.558zM6.761 8.83l-6.57 4.027A2 2 0 0 0 2 14h12a2 2 0 0 0 1.808-1.144l-6.57-4.027L8 9.586zm3.436-.586L16 11.801V4.697z" />
                        </svg>
                    </span>
                    <div className="form-floating">
                        <input type="email" className="form-control" id="email" placeholder="Email" value={user.email} onChange={handleChange} />
                        <label htmlFor="email">Email address</label>
                    </div>
                </div>

                <div className="input-group">
                    <span className="input-group-text" id="loginPassword">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" className="bi bi-key" viewBox="0 0 16 16">
                            <path d="M0 8a4 4 0 0 1 7.465-2H14a.5.5 0 0 1 .354.146l1.5 1.5a.5.5 0 0 1 0 .708l-1.5 1.5a.5.5 0 0 1-.708 0L13 9.207l-.646.647a.5.5 0 0 1-.708 0L11 9.207l-.646.647a.5.5 0 0 1-.708 0L9 9.207l-.646.647A.5.5 0 0 1 8 10h-.535A4 4 0 0 1 0 8m4-3a3 3 0 1 0 2.712 4.285A.5.5 0 0 1 7.163 9h.63l.853-.854a.5.5 0 0 1 .708 0l.646.647.646-.647a.5.5 0 0 1 .708 0l.646.647.646-.647a.5.5 0 0 1 .708 0l.646.647.793-.793-1-1h-6.63a.5.5 0 0 1-.451-.285A3 3 0 0 0 4 5" />
                            <path d="M4 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0" />
                        </svg>
                    </span>
                    <div className="form-floating">
                        <input type="password" className="form-control" id="password" placeholder="Password" value={user.password} onChange={handleChange} />
                        <label htmlFor="password">Password</label>
                    </div>
                </div>
                <div className="lean text-danger my-2">{errors.general}</div>
                <SubmitButton />
                <div className="lead text-center text-white">
                    Do not have an account? <Link className="text-white" to="/register">Register</Link>
                </div>
            </form>
        </main>
    );
}