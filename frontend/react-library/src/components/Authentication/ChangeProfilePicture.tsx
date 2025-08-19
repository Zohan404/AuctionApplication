import { useState } from "react";
import { SubmitButton } from "../Utils/SubmitButton";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";

export const ChangeProfilePicture = () => {
    const { handleLogOut, changeLoggedInUser } = useAuthService();
    const [previewUrl, setPreviewUrl] = useState<string | null>(null);
    const [file, setFile] = useState<File | null>(null);
    const { errors, setFieldError, handleBackendErrors, clearErrors } = useFormErrors();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file && file.size > 10 * 1024 * 1024) {
            setFieldError("general", "File size exceeds 10MB!");
            return;
        }
        setFile(file || null);
        setPreviewUrl(file ? URL.createObjectURL(file) : null);
    }

    const handleUpload = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();
        if (!file) {
            setFieldError("general", "Picture must be uploaded.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await fetch("http://localhost:8081/api/users/me/profile-picture", {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`
                },
                body: formData
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

        } catch (err: any) {
            setFieldError("general", err.message);
        }
    };

    return (
        <div className="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
            <h1 className="mb-5 fw-bold text-center">Change profile picture</h1>
            <form className="w-50" onSubmit={handleUpload}>
                <div>
                    <div className="input-group mb-3">
                        <label htmlFor="file-upload" className="input-group-text">
                            Upload
                        </label>
                        <input type="text" className="form-control" placeholder="Select a picture" aria-label="Upload"
                            aria-describedby="basic-addon1" value={file ? file.name : ""} readOnly />
                    </div>
                    <input type="file" id="file-upload" className="form-control d-none" onChange={handleChange} />
                </div>
                {previewUrl &&
                    <div className="mt-3 d-flex justify-content-center">
                        <img src={previewUrl} alt="Profilkép" width={200} />
                    </div>
                }
                {errors.general && (<div className="text-danger">{errors.general}</div>)}
                <SubmitButton />
            </form>
        </div>

    );
};