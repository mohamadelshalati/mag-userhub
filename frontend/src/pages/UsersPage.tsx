import { useEffect, useState } from "react";
import { getAllUsers } from "../api/usersApi";
import SearchUsers from "../components/SearchUsers";
import Filter from "../components/Filter";
import CreateUserModal from "../components/CreateUserModal";
import type { User } from "../models/user";
import type { Page } from "../models/page";
import { useNavigate } from "react-router-dom";

type Column = {
  key: string;
  label: string;
};

const COLUMNS: Column[] = [
  { key: "id", label: "ID" },
  { key: "firstName", label: "First Name" },
  { key: "lastName", label: "Last Name" },
  { key: "email", label: "Email" },
  { key: "profession", label: "Profession" },
  { key: "city", label: "City" },
  { key: "country", label: "Country" },
  { key: "dateCreated", label: "Date Created" },
];

export default function Users() {
  const [pageData, setPageData] = useState<Page<User> | null>(null);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [page, setPage] = useState<string>("0");
  const [search, setSearch] = useState<string>("");
  const [country, setCountry] = useState("");
  const [city, setCity] = useState("");
  const [profession, setProfession] = useState("");
  const [showCreateUser, setShowCreateUser] = useState(false);
  const [createdUser, setCreatedUser] = useState<User>();
  const navigate = useNavigate();

  useEffect(() => {
    let active = true;
    getAllUsers({
      search,
      profession,
      country,
      city,
      page,
      sortBy,
      sortDir,
    }).then((res) => {
      if (active) setPageData(res);
    });
    return () => {
      active = false;
    };
  }, [page, sortBy, sortDir, profession, country, city, createdUser, search]);

  const canGoNext =
    pageData?.number !== undefined &&
    pageData?.totalPages !== undefined &&
    pageData.totalPages > pageData.number + 1;

  const canGoPrevious = pageData?.number !== undefined && pageData.number > 0;

  const nextClicked = () => {
    if (canGoNext && pageData) {
      setPage(String(pageData.number + 1));
    }
  };

  const previousClicked = () => {
    if (canGoPrevious && pageData) {
      setPage(String(pageData.number - 1));
    }
  };

  const headerClicked = (column: string) => {
    if (sortBy === column) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(column);
      setSortDir("asc");
    }
  };

  const handleSearch = async () => {
    const res = await getAllUsers({
      search,
      profession,
      country,
      city,
      page,
      sortBy,
      sortDir,
    });
    return setPageData(res);
  };

  function handleRowClick(id: number) {
    navigate(`/users/${id}`);
  }

  const sortIndicator = (column: string) => {
    if (sortBy !== column) return "↕";
    return sortDir === "asc" ? "↑" : "↓";
  };

  return (
    <div>
      <CreateUserModal
        isOpen={showCreateUser}
        onClose={() => setShowCreateUser(false)}
        onCreated={(user: User) => {
          setCreatedUser(user);
          navigate(`/users/${user.id}`);
        }}
      />
      <div className="my-5">
        <div className="grid grid-cols-2 justify-items-start w-3/4">
          <SearchUsers
            value={search}
            searchButtonDisabled={search.length === 0}
            onChange={setSearch}
            onSearch={handleSearch}
          />
          <button
            type="button"
            className="inline-flex items-center ml-2 gap-2 rounded-md bg-gray-900 px-4 py-2
             text-sm font-medium text-white shadow-sm transition
             hover:bg-gray-700 focus:outline-none focus:ring-2
             focus:ring-gray-400 focus:ring-offset-2"
            onClick={() => setShowCreateUser(true)}
          >
            <span className="text-lg leading-none">+</span>
            Create User
          </button>
        </div>

        <div className="mt-5 grid grid-cols-3">
          <Filter
            label="Country"
            value={country}
            fetchTerm={"countries"}
            onChange={setCountry}
          />
          <Filter
            label="City"
            value={city}
            fetchTerm={"cities"}
            onChange={setCity}
          />
          <Filter
            label="Profession"
            value={profession}
            fetchTerm={"professions"}
            onChange={setProfession}
          />
        </div>
      </div>
      <div className="overflow-scroll rounded-lg border border-gray-200 bg-white shadow-sm">
        <table className="w-full table-fixed divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {COLUMNS.map(({ key, label }) => (
                <th
                  key={key}
                  className="cursor-pointer select-none px-6 py-5 text-left text-xs font-medium uppercase tracking-wider text-gray-500"
                  onClick={() => headerClicked(key)}
                  data-column={key}
                >
                  <span className="flex items-center gap-1 whitespace-nowrap">
                    <span>{label}</span>
                    <span className="inline-block w-3 shrink-0 text-center">
                      {sortIndicator(key)}
                    </span>
                  </span>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 bg-white">
            {pageData?.content.map((user: User) => (
              <tr
                key={user.id}
                className="cursor-pointer transition hover:bg-gray-50"
                onClick={() => handleRowClick(user.id)}
              >
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                  {user.id}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                  {user.firstName}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                  {user.lastName}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-500">
                  {user.email}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-500">
                  {user.profession}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-500">
                  {user.city}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-500">
                  {user.country}
                </td>
                <td className="truncate whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-500">
                  {user.dateCreated.slice(0, 10)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="flex items-center justify-between px-6 py-4">
        <button
          onClick={previousClicked}
          disabled={!canGoPrevious}
          className="rounded-md border border-gray-300 px-4 py-2 text-sm hover:bg-gray-50 disabled:opacity-50 disabled:bg-white"
        >
          Previous
        </button>

        <span className="text-sm text-gray-500">
          {pageData?.number !== undefined && pageData?.numberOfElements > 0
            ? pageData.number + 1
            : 0}
          {" of "}
          {pageData?.totalPages !== undefined ? pageData.totalPages : 0}
        </span>

        <button
          onClick={nextClicked}
          disabled={!canGoNext}
          className="rounded-md border border-gray-300 px-4 py-2 text-sm hover:bg-gray-50 disabled:opacity-50 disabled:bg-white"
        >
          Next
        </button>
      </div>
    </div>
  );
}
