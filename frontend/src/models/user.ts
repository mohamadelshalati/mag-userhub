export type User = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  city: string;
  country: string;
  profession: string;
  dateCreated: string;
};

export type UserDto = Omit<User, "id" | "dateCreated">;
