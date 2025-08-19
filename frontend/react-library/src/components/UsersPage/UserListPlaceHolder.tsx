import { FollowersTabPlaceHolder } from "../Utils/FollowersTabPlaceHolder";

export const UserListPlaceHolder: React.FC<{
    shadow: boolean;
}> = (props) => {
    return (
        <li className="list-group-item border-0">
            <div className={`d-flex justify-content-between align-items-center border border-2 ${props.shadow} p-3`}>
                <div className="ms-md-2 d-flex">
                    <div className="rounded-circle bg-secondary" style={{ width: 90, height: 90 }}></div>
                    <div className="d-flex flex-column gap-2 align-content-center ms-2">
                        <div className="placeholder-glow text-center">
                            <span className="placeholder col-8 display-6"></span>
                        </div>
                        <FollowersTabPlaceHolder size="h6" />
                    </div>
                </div>
                <div className="d-flex gap-3 me-5">
                    <div className="placeholder-glow d-flex flex-column align-items-center">
                        <span className="placeholder rounded-pill bg-primary" style={{ width: 40, height: 20 }}></span>
                        <span className="text-secondary text-center mt-1">ongoing auctions</span>
                    </div>
                    <div className="vr"></div>
                    <div className="placeholder-glow d-flex flex-column align-items-center">
                        <span className="placeholder rounded-pill bg-dark" style={{ width: 40, height: 20 }}></span>
                        <span className="text-secondary text-center mt-1">sold items</span>
                    </div>
                </div>
            </div>
        </li>
    );
}