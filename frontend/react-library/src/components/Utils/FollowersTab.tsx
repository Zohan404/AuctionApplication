import { UserListModal } from "../UsersPage/UserListModal";

export const FollowersTab: React.FC<{
    followers: number | undefined,
    followings: number | undefined,
    button: boolean,
    size: string
}> = (props) => {
    return (
        <div className="d-flex justify-content-center">
            <div className={`d-flex flex-nowrap gap-1 ${props.size} px-2 fw-normal`}>
                {
                    props.button
                        ?
                        <UserListModal type={"followers"} size={props.size} />
                        :
                        "Followers"
                }
                <span className="badge bg-dark align-content-center">
                    {props.followers}
                </span>
            </div>
            <div className="vr"></div>
            <div className={`d-flex felx-nowrap gap-1 ${props.size} px-2 fw-normal`}>
                <span className="badge bg-dark align-content-center">
                    {props.followings}
                </span>
                {
                    props.button
                        ?
                        <UserListModal type={"followings"} size={props.size} />
                        :
                        "Followings"
                }
            </div>
        </div>
    );
}