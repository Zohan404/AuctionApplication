import { useState } from "react";
import { Link } from "react-router-dom";
import { User } from "../Models/User";
import { CountryDropdown } from "../Utils/CountryDropdown";
import { SubmitButton } from "../Utils/SubmitButton";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";

export const UserDataForm: React.FC<{
    method: "make" | "update";
    onSubmit: (user: User) => void;
    errors: Record<string, string>
}> = (props) => {
    const { getLoggedInUser } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const { clearErrors } = useFormErrors();

    const [formData, setFormData] = useState<User>({
        firstName: loggedInUser ? loggedInUser.firstName : "",
        middleName: loggedInUser ? loggedInUser.middleName : "",
        lastName: loggedInUser ? loggedInUser.lastName : "",
        email: loggedInUser ? loggedInUser.email : "",
        password: loggedInUser ? loggedInUser.password : "",
        dateOfBirth: loggedInUser ? loggedInUser.dateOfBirth : undefined,
        location: loggedInUser ? loggedInUser.location : "NO_COUNTRY",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { id, value } = e.target;
        setFormData(prev => ({ ...prev, [id]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();
        props.onSubmit(formData);
        console.log(props.errors);
    };

    const maxDate = () => {
        const today = new Date();
        return new Date(Date.now() - 1000 * 60 * (60 * 24 + today.getTimezoneOffset())).toISOString().split('T')[0];
    }

    return (
        <div className={`container d-flex flex-column justify-content-center align-items-center ${props.method === "make" ? "mt-5 pt-5" : "mt-3 mb-5"}`}>
            <h1 className="mb-5 fw-medium text-center">{props.method === "make" ? "Registration" : "Update user data"}</h1>
            {props.errors.general && <p className="text-bg-danger">Unexpected error happened.</p>}
            <form className="mx-5 needs-validation d-flex flex-column" noValidate onSubmit={handleSubmit}>
                <label htmlFor="firstName" className="form-label">Name</label>
                <div className="row text-black g-3 mb-3">
                    <div className="col-md">
                        <div className="form-floating">
                            <input type="text" className={`form-control ${props.errors.firstName && "is-invalid"}`} id="firstName" value={formData.firstName} onChange={handleChange} placeholder="First name" required />
                            <label htmlFor="firstName">First name</label>
                            {props.errors.firstName && <div className="invalid-feedback ">{props.errors.firstName}</div>}
                        </div>
                    </div>
                    <div className="col-md">
                        <div className="form-floating">
                            <input type="text" className={`form-control ${props.errors.middleName && "is-invalid"}`} id="middleName" value={formData.middleName} onChange={handleChange} placeholder="Middle name" />
                            <label htmlFor="middleName">Middle name</label>
                            {props.errors.middleName && <div className="invalid-feedback">{props.errors.middleName}</div>}
                        </div>
                    </div>
                    <div className="col-md">
                        <div className="form-floating">
                            <input type="text" className={`form-control ${props.errors.lastName && "is-invalid"}`} id="lastName" value={formData.lastName} onChange={handleChange} placeholder="Last name" required />
                            <label htmlFor="lastName">Last name</label>
                            {props.errors.lastName && <div className="invalid-feedback">{props.errors.lastName}</div>}
                        </div>
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md">
                        <label htmlFor="email" className="form-label">Email Address</label>
                        <input type="email" readOnly={props.method === "update"} disabled={props.method === "update"}
                            className={`form-control ${props.errors.email && "is-invalid"}`}
                            id="email" value={formData.email} onChange={handleChange} aria-describedby="emailHelp" />
                        {props.errors.email && <div className="invalid-feedback">{props.errors.email}</div>}
                    </div>
                    <div className="col-md d-flex flex-column justify-content-end">

                        {
                            props.method === "make"
                                ?
                                <>
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input type="password" className={`form-control ${props.errors.password && "is-invalid"}`}
                                        id="password" value={formData.password} onChange={handleChange} />
                                    {props.errors.password && <div className="invalid-feedback">{props.errors.password}</div>}
                                </>
                                :
                                <Link className="btn btn-outline-success m-3 m-md-0" to="change-password">
                                    Change Password
                                </Link>
                        }
                    </div>
                </div>
                <div className="row mb-3">
                    <div className="col-md">
                        <label htmlFor="dateOfBirth" className="form-label">Date of Birth</label>
                        <input type="date" className={`form-control ${props.errors.dateOfBirth && "is-invalid"}`}
                            id="dateOfBirth" max={maxDate()} value={formData.dateOfBirth === undefined
                                ? "" : formData.dateOfBirth.toString().split('T')[0]} onChange={handleChange} />
                        {props.errors.dateOfBirth && <div className="invalid-feedback">{props.errors.dateOfBirth}</div>}
                    </div>
                    <div className="col-md">
                        <label htmlFor="location" className="form-label">Current Location</label>
                        <CountryDropdown buttonColor="btn-light" initialCountry={formData.location}
                            onSelected={(country: string) => setFormData(prev => ({ ...prev, location: country }))} />
                        {props.errors.location && <div className="invalid-feedback">{props.errors.location}</div>}
                    </div>
                </div>
                {props.method === "update" &&
                    <div className="d-flex flex-column">
                        <Link className="btn btn-outline-success" to="change-profile-picture">
                            Change Profile Picture
                        </Link>
                    </div>
                }
                <SubmitButton />
                {props.method === "make" &&
                    <div className="lead text-center text-white mb-5 pb-5">
                        Already registered? Back to <Link className="text-white" to="/login">login</Link>
                    </div>
                }
            </form>
        </div>
    );
}