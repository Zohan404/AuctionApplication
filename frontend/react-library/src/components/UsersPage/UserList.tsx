import { Link } from "react-router-dom"
import { ListedUserModel } from "../Models/ListedUserModel"
import { FollowersTab } from "../Utils/FollowersTab"
import { ProfilePicture } from "../Utils/ProfilePicture"
import { nameShower } from "../Utils/helper"

export const UserList: React.FC<{
    user: ListedUserModel;
    shadow: boolean;
    onClick?: () => void
}> =
    (props) => {
        return (
            <li className="list-group-item border-0">
                <Link to={`/users/${props.user.id}`} className="text-decoration-none text-dark" onClick={props.onClick}>
                    <div className={`d-flex justify-content-between align-items-center border border-2 ${props.shadow && "shadow"} p-3`}>
                        <div className="ms-md-2 d-flex">
                            <ProfilePicture pictureUrl={props.user.profilePicture} rounded={true} size={90} />
                            <div className="d-flex flex-column gap-2 align-content-center ms-2">
                                <div className="display-6 fw-bold text-center">{nameShower(props.user)}</div>
                                <FollowersTab followers={props.user.followers?.length} followings={props.user.followings?.length} button={false} size="h6" />
                            </div>
                        </div>
                        <div className="d-flex gap-3 me-5">
                            <div className="d-flex flex-column align-items-center">
                                <span className="badge bg-primary rounded-pill w-auto">{props.user.onGoingAuctions?.length}</span>
                                <span className="text-secondary text-center">ongoing auctions</span>
                            </div>
                            <div className="vr"></div>
                            <div className="d-flex flex-column align-items-center">
                                <span className="badge bg-dark rounded-pill w-auto">{props.user.soldItems?.length}</span>
                                <span className="text-secondary text-center">sold items</span>
                            </div>
                        </div>
                    </div>
                </Link>
            </li>
        )
    }