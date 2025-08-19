import { Navigate, Route, Routes } from 'react-router-dom';
import './App.css';
import { AuctionItemPage } from './components/AuctionPage/AuctionItemPage/AuctionItemPage';
import { AuctionPage } from './components/AuctionPage/AuctionPage';
import { NewAuction } from './components/AuctionPage/NewAuction';
import { ChangePassword } from './components/Authentication/ChangePassword';
import { ChangeProfilePicture } from './components/Authentication/ChangeProfilePicture';
import { LoginPage } from './components/Authentication/LoginPage';
import { ProfilePage } from './components/Authentication/ProfilePage';
import { RegistrationPage } from './components/Authentication/RegistrationPage';
import { ErrorPage } from './components/ErrorPage/ErrorPage';
import { HomePage } from './components/HomePage/HomePage';
import { Footer } from './components/NavBarAndFooter/Footer';
import { Navbar } from './components/NavBarAndFooter/Navbar';
import { UserAuctionsSubPage } from './components/UsersPage/UserItemPage/UserAuctionsSubPage';
import { UserBidsSubPage } from './components/UsersPage/UserItemPage/UserBidsSubPage';
import { UserItemPage } from './components/UsersPage/UserItemPage/UserItemPage';
import { UserReviewsSubPage } from './components/UsersPage/UserItemPage/UserReviewsSubPage';
import { UsersPage } from './components/UsersPage/UsersPage';
import { ProtectedRoute } from './components/Utils/ProtectedRoute';

const LayoutWrapper = ({ children }: { children: React.ReactNode }) => (
    <div className='d-flex flex-column min-vh-100'>
      <Navbar />
      <div className='flex-grow-1'>
        {children}
      </div>
      <Footer />
    </div>
);


export const App = () => {
  return (
    <Routes>
      <Route path='/login' element={<ProtectedRoute location={"/home"} needsAuth={false}><LoginPage /></ProtectedRoute>} />
      <Route path='/register' element={<ProtectedRoute location={"/home"} needsAuth={false}><RegistrationPage /></ProtectedRoute>} />
      <Route index element={<Navigate to='/home' />} />
      <Route path='/home' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><HomePage /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/auctions/new' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><NewAuction /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/auctions/:auctionId' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><AuctionItemPage /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/auctions' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><AuctionPage /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/users/:userId' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><UserItemPage /></ProtectedRoute></ LayoutWrapper>}>
        <Route index element={<Navigate to="auctions" />} />
        <Route path='auctions' element={<UserAuctionsSubPage />} />
        <Route path='reviews' element={<UserReviewsSubPage />} />
        <Route path='bids' element={<UserBidsSubPage />} />
      </Route>
      <Route path='/users' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><UsersPage /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/profile' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><ProfilePage /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/profile/change-password' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><ChangePassword /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='/profile/change-profile-picture' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><ChangeProfilePicture /></ProtectedRoute></ LayoutWrapper>} />
      <Route path='*' element={<LayoutWrapper><ProtectedRoute location={"/login"} needsAuth={true}><ErrorPage /></ProtectedRoute></ LayoutWrapper>} />
    </Routes >
  );
}
