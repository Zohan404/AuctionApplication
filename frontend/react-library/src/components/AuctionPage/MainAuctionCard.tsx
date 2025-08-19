import React from "react";
import { Link } from "react-router-dom";
import { ListedAuctionModel } from "../Models/ListedAuctionModel";
import { ProfilePicture } from "../Utils/ProfilePicture";
import { dateToString, getTransformedImageUrl, nameShower } from "../Utils/helper";

export const MainAuctionCard: React.FC<{
    auction: ListedAuctionModel
}> = (props) => {
    const dateCounter = (givenDate: Date) => {
        const now: Date = new Date();
        const date: Date = new Date(givenDate);
        const difference = now.getTime() - date.getTime();

        const seconds = Math.floor(difference / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        const months = Math.floor(days / 30);
        const years = Math.floor(days / 365);

        if (years > 0) return `${years} hours`;
        if (months > 0) return `${months} months`;
        if (days > 0) return `${days} days`;
        if (hours > 0) return `${hours} hours`;
        if (minutes > 0) return `${minutes} minutes`;
        else return "A few seconds"
    }

    return (
        <Link to={`/auctions/${props.auction.id}`} className="text-decoration-none">
            <div className="card shadow h-100">
                <img src={getTransformedImageUrl(props.auction.frontPicture, 1500, 1000)} className="card-img-top" alt="1" />
                {props.auction.createdBy &&
                    <div className="card-img-overlay">
                        <div className="d-flex align-items-center gap-2">
                            <ProfilePicture pictureUrl={props.auction.createdBy.profilePicture} rounded={true} size={90} />
                            <h5 className="card-title text-bg-dark bg-opacity-25 fw-bold">
                                {nameShower(props.auction.createdBy)}
                            </h5>
                        </div>

                    </div>
                }
                <div className="card-body main-color">
                    <div className="row">
                        <div className="col-8">
                            <h5 className="card-title text-white">{props.auction.title}</h5>
                            <h6 className="card-subtitle mb-2 text-white-50">
                                {dateToString(props.auction.startDate)} - {dateToString(props.auction.endDate)}
                            </h6>
                        </div>
                        <div className="col-4">
                            <h4 className="text-dark fw-bold text-white">{props.auction.nextPrice}$</h4>
                        </div>
                    </div>
                    <p className="card-text text-truncate text-white">{props.auction.description}</p>
                </div>

                <div className="card-footer secondary-color text-body-secondary text-center">
                    {dateCounter(props.auction.createdTime) + " ago"}
                </div>
                {props.auction.isSecret &&
                    <span className="position-absolute top-0 start-100 translate-middle bg-danger px-1 pb-1 border border-light rounded-circle">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-lock-fill" viewBox="0 0 16 16">
                            <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2m3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2" />
                        </svg>
                    </span>}
            </div>
        </Link>
    );
}