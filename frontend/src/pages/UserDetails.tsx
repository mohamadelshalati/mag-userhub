import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { deleteUserById, getUserById } from "../api/usersApi";
import type { User } from "../models/user";

export default function UserDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const data = await getUserById(Number(id));
        setUser(data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id]);

  const handleDelete = async () => {
    if (!user) return;

    const confirmed = window.confirm(
      `Are you sure you want to delete ${user.firstName} ${user.lastName}?`,
    );

    if (!confirmed) return;

    try {
      setDeleting(true);

      await deleteUserById(Number(id));
      navigate("/users");
    } catch (error) {
      console.error(error);
      setDeleting(false);
    }
  };

  if (loading) {
    return <div className="p-6">Loading user...</div>;
  }

  if (!user) {
    return (
      <div>
        <div className="p-6">User not found.</div>
        <button
          type="button"
          onClick={() => navigate("/users")}
          className="cursor-pointer mt-6 ml-5 text-sm font-medium text-gray-600 hover:text-gray-900"
        >
          ← Back to Users
        </button>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl px-6 py-10">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">
            {user.firstName} {user.lastName}
          </h1>

          <p className="mt-1 text-sm text-gray-500">User #{user.id}</p>
        </div>

        <button
          type="button"
          onClick={handleDelete}
          disabled={deleting}
          className="rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {deleting ? "Deleting..." : "Delete User"}
        </button>
      </div>

      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div>
            <p className="text-sm text-gray-500">First Name</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.firstName}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Last Name</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.lastName}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Email</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.email}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Profession</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.profession}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Country</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.country}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">City</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.city}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Date Created</p>
            <p className="mt-1 text-sm font-medium text-gray-900">
              {user.dateCreated}
            </p>
          </div>
        </div>
      </div>

      <button
        type="button"
        onClick={() => navigate("/users")}
        className="cursor-pointer mt-6 text-sm font-medium text-gray-600 hover:text-gray-900"
      >
        ← Back to Users
      </button>
    </div>
  );
}
