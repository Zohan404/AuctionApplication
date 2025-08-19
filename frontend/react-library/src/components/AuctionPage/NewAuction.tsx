import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Auction } from "../Models/Auction";
import { ListedUserModel } from "../Models/ListedUserModel";
import { User } from "../Models/User";
import { SubmitButton } from "../Utils/SubmitButton";
import { useAuthService } from "../Utils/useAuthService";
import { useFormErrors } from "../Utils/useFormErrors";

export const NewAuction = () => {
    const navigate = useNavigate();
    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const user: ListedUserModel = new ListedUserModel(
        loggedInUser?.id ? loggedInUser.id : 0,
        loggedInUser?.firstName ? loggedInUser.firstName : "",
        loggedInUser?.lastName ? loggedInUser.lastName : "",
        loggedInUser?.location ? loggedInUser.location : "",
        loggedInUser?.profilePicture ? loggedInUser.profilePicture : "",
        [], [], [], [], loggedInUser?.middleName ? loggedInUser.middleName : ""
    )

    const [auction, setAuction] = useState<Auction>({
        id: 0,
        title: "",
        description: "",
        startingPrice: 0,
        bidIncrement: 0,
        buyNowPrice: 0,
        startDate: undefined,
        endDate: undefined,
        pictures: [],
        isSecret: false,
        createdBy: user
    });
    const [files, setFiles] = useState<File[]>([]);
    const { errors, setFieldError, handleBackendErrors, clearErrors } = useFormErrors();

    const handleFilesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFiles = e.target.files;
        if (selectedFiles) {
            const newFiles = Array.from(selectedFiles);
            if (newFiles.some(file => file.size > 10 * 1024 * 1024)) {
                setFieldError("pictures", "One or more files exceed 10MB!");
                return;
            }
            setFiles(newFiles);
        }
    }

    const minDateFrom = (date: Date, offset: number) => {
        date = new Date(date);
        return new Date(date.getTime() + 1000 * 60 * (offset * (60 * 24) - date.getTimezoneOffset())).toISOString().split('T')[0];
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { id, value } = e.target;
        setAuction(prev => ({ ...prev, [id]: value }));
    };

    const handleUpload = async (e: React.FormEvent) => {
        e.preventDefault();
        clearErrors();

        const formData = new FormData();
        const auctionToSend = {
            ...auction,
            isSecret: auction.isSecret ? 1 : 0
        };
        const jsonAuction = JSON.stringify(auctionToSend);
        formData.append("auction", new Blob([jsonAuction], { type: "application/json" }));
        files.forEach(file => {
            formData.append("files", file);
        });

        const response = await fetch("http://localhost:8081/api/auctions", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            },
            body: formData
        });

        if (!response.ok) {
            await handleBackendErrors(response);
            if (files.length === 0) {
                setFieldError("pictures", "At least one picture has to be uploaded.");
            }
            if (auction.bidIncrement === null && !auction.isSecret) {
                setFieldError("bidIncrement", "Bid increment is required.");
            }
            if (auction.buyNowPrice === null && !auction.isSecret) {
                setFieldError("buyNowPrice", "Buy now price is required.");
            }
            return;
        }
        if (response.status === 401 || response.status === 403) {
            handleLogOut();
            return Promise.reject(new Error('Authentication failed'));
        }


        alert("Auction created successfully!");
        const user: string | null = localStorage.getItem("user");
        const userId: string = user ? JSON.parse(user).id : null;
        if (userId) {
            navigate(`/users/${userId}`);
        }
    };

    return (
        <main className="my-5">
            <div className="container mt-5">
                <h1 className="text-center">Create a new auction</h1>
                <form className="mt-4" onSubmit={handleUpload}>
                    <div className="row mb-3 form-check form-switch">
                        <input type="checkbox" id="isSecret" name="isSecret" className="form-check-input"
                            checked={auction.isSecret} onChange={(e) => setAuction(prev => ({ ...prev, isSecret: e.target.checked, startingPrice: 0, bidIncrement: e.target.checked ? undefined : 0, buyNowPrice: e.target.checked ? undefined : 0 }))} />
                        <label htmlFor="isSecret" className="form-check-label">Secret auction</label>
                    </div>
                    <div className="row mb-3">
                        <label htmlFor="title" className="form-label">Title</label>
                        <input type="text" className={`form-control ${errors.title && "is-invalid"}`} id="title" name="title"
                            value={auction.title} onChange={handleChange} />
                        <div className="invalid-feedback">{errors.title}</div>
                    </div>
                    <div className="row mb-3">
                        <label htmlFor="description" className="form-label">Description</label>
                        <textarea className={`form-control ${errors.description && "is-invalid"}`} id="description" name="description" rows={3}
                            value={auction.description} onChange={handleChange}></textarea>
                        <div className="invalid-feedback">{errors.description}</div>
                    </div>
                    <div className="row">
                        <div className="col mb-3">
                            <label htmlFor="startingPrice" className="form-label">Starting Price</label>
                            <input type="number" className={`form-control ${errors.startingPrice && "is-invalid"}`} id="startingPrice" name="startingPrice"
                                value={auction.startingPrice} onChange={handleChange} min={0} />
                            <div className="invalid-feedback">{errors.startingPrice}</div>
                        </div>
                        {!auction.isSecret && <>
                            <div className="col mb-3">
                                <label htmlFor="bidIncrement" className="form-label">Bid Increment</label>
                                <input type="number" className={`form-control ${errors.bidIncrement && "is-invalid"}`} id="bidIncrement" name="bidIncrement"
                                    value={auction.bidIncrement} onChange={handleChange} min={0} />
                                <div className="invalid-feedback">{errors.bidIncrement}</div>
                            </div>
                            <div className="col mb-3">
                                <label htmlFor="buyNowPrice" className="form-label">Buy Now Price</label>
                                <input type="number" className={`form-control ${errors.buyNowPrice && "is-invalid"}`} id="buyNowPrice" name="buyNowPrice"
                                    value={auction.buyNowPrice} onChange={handleChange} min={0} />
                                <div className="invalid-feedback">{errors.buyNowPrice}</div>
                            </div>
                        </>}
                    </div>
                    <div className="row">
                        <div className="col mb-3">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <input type="date" className={`form-control ${errors.startDate && "is-invalid"}`} id="startDate" name="startDate"
                                value={auction.startDate === undefined ? "" : auction.startDate.toString().split('T')[0]}
                                onChange={handleChange} min={minDateFrom(new Date(), 0)} disabled={auction.endDate !== undefined} />
                            <div className="invalid-feedback">{errors.startDate}</div>
                        </div>
                        <div className="col mb-3">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <input type="date" className={`form-control ${errors.endDate && "is-invalid"}`} id="endDate" name="endDate"
                                value={auction.endDate === undefined ? "" : auction.endDate.toString().split('T')[0]}
                                onChange={handleChange} min={auction.startDate && minDateFrom(auction.startDate, 1)} disabled={auction.startDate === undefined} />
                            <div className="invalid-feedback">{errors.endDate}</div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="input-group mb-3">
                            <label htmlFor="file-upload" className="input-group-text">
                                Upload
                            </label>
                            <input type="text" className={`form-control ${errors.pictures && "is-invalid"}`}
                                placeholder="Select a picture" aria-label="Upload" aria-describedby="basic-addon1" readOnly
                                value={files.length > 0 ? files.map(file => file.name).join(" ; ") : ""} />
                            {errors.pictures && <div className="invalid-feedback">{errors.pictures}</div>}
                        </div>
                        <input type="file" id="file-upload" className="form-control d-none" onChange={handleFilesChange} multiple />
                    </div>
                    {errors.general && <div className="text-danger">{errors.general}</div>}
                    <SubmitButton />
                </form>
            </div >
        </main >
    );
}