import { useState } from "react";
import { createUser } from "../api/usersApi";
import type { User, UserDto } from "../models/user";

type CreateUserModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onCreated: (user: User) => void;
};

const initFormData = {
  firstName: "",
  lastName: "",
  email: "",
  profession: "",
  country: "",
  city: "",
};

export default function CreateUserModal({
  isOpen,
  onClose,
  onCreated,
}: CreateUserModalProps) {
  const [userForm, setUserForm] = useState<UserDto>(initFormData);
  const [errors, setErrors] = useState<Partial<UserDto>>({});

  if (!isOpen) {
    return null;
  }

  const hadnleSubmit = async (event: React.SubmitEvent) => {
    event.preventDefault();

    const response = await createUser(userForm);

    if (response.errors) {
      setErrors(response.errors);
      return;
    }
    setErrors({});
    setUserForm(initFormData);
    onCreated(response);
    onClose();
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;

    setUserForm((currentFormData) => ({
      ...currentFormData,
      [name]: value,
    }));

    setErrors((currentErrors) => ({
      ...currentErrors,
      [name]: undefined,
    }));
  };

  const inputClass = (field: keyof UserDto) =>
    `w-full rounded-md border px-3 py-2 outline-none ${
      errors[field]
        ? "border-red-500 focus:ring-1 focus:ring-red-500"
        : "border-gray-300 focus:border-gray-500 focus:ring-1 focus:ring-gray-500"
    }`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="w-full max-w-lg rounded-lg bg-white p-6 shadow-xl">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-gray-900">Create User</h2>

          <button
            type="button"
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            ✕
          </button>
        </div>

        <form onSubmit={hadnleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <input
                name="firstName"
                value={userForm.firstName}
                onChange={handleChange}
                placeholder="First name"
                className={inputClass("firstName")}
              />

              {errors.firstName && (
                <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>
              )}
            </div>

            <div>
              <input
                name="lastName"
                value={userForm.lastName}
                onChange={handleChange}
                placeholder="Last name"
                className={inputClass("lastName")}
              />

              {errors.lastName && (
                <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>
              )}
            </div>
          </div>

          <div>
            <input
              name="email"
              value={userForm.email}
              onChange={handleChange}
              placeholder="Email"
              type="email"
              className={inputClass("email")}
            />

            {errors.email && (
              <p className="mt-1 text-sm text-red-600">{errors.email}</p>
            )}
          </div>

          <div>
            <input
              name="profession"
              value={userForm.profession}
              onChange={handleChange}
              placeholder="Profession"
              className={inputClass("profession")}
            />

            {errors.profession && (
              <p className="mt-1 text-sm text-red-600">{errors.profession}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <input
                name="country"
                value={userForm.country}
                onChange={handleChange}
                placeholder="Country"
                className={inputClass("country")}
              />

              {errors.country && (
                <p className="mt-1 text-sm text-red-600">{errors.country}</p>
              )}
            </div>

            <div>
              <input
                name="city"
                value={userForm.city}
                onChange={handleChange}
                placeholder="City"
                className={inputClass("city")}
              />

              {errors.city && (
                <p className="mt-1 text-sm text-red-600">{errors.city}</p>
              )}
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="rounded-md border border-gray-300 px-4 py-2 text-sm hover:bg-gray-100"
            >
              Cancel
            </button>

            <button
              type="submit"
              className="rounded-md bg-gray-900 px-4 py-2 text-sm text-white hover:bg-gray-700 disabled:bg-gray-400"
            >
              Create User
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
