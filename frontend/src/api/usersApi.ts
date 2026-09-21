import type { UserDto as UserDto } from "../models/user";

const BASE_URL = "http://localhost:8080";

type GetAllUsersParams = {
  search?: string;
  profession?: string;
  country?: string;
  city?: string;
  page?: string;
  size?: string;
  sortBy?: string;
  sortDir?: string;
};

export async function getAllUsers({
  search = "",
  profession = "",
  country = "",
  city = "",
  page = "0",
  size = "25",
  sortBy = "id",
  sortDir = "asc",
}: GetAllUsersParams = {}) {
  const params = new URLSearchParams();

  if (search) params.append("search", search);
  if (profession) params.append("profession", profession);
  if (country) params.append("country", country);
  if (city) params.append("city", city);

  params.append("page", page);
  params.append("size", size);
  params.append("sortBy", sortBy);
  params.append("sortDir", sortDir);

  const response = await fetch(`${BASE_URL}/api/users?${params.toString()}`);

  if (!response.ok) {
    throw new Error("Failed to fetch users");
  }

  return response.json();
}

export async function getUserById(id: number) {
  const response = await fetch(`${BASE_URL}/api/users/${id}`);

  if (!response.ok) {
    throw new Error("Failed to fetch user");
  }

  return response.json();
}

export async function deleteUserById(id: number) {
  const response = await fetch(`${BASE_URL}/api/users/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Failed to fetch user");
  }
}

export async function getFilterOptions(filter: string) {
  const response = await fetch(`${BASE_URL}/api/users/${filter}`);

  if (!response.ok) {
    throw new Error("Failed to fetch options");
  }

  return response.json();
}

export async function createUser(userDto: UserDto) {
  const response = await fetch(`${BASE_URL}/api/users`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ ...userDto }),
  });

  if (response.status === 400 || response.status === 422) {
    return response.json();
  }

  if (!response.ok) {
    throw new Error("Failed to create user");
  }

  return response.json();
}
