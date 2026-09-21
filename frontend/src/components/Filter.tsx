import { useEffect, useState } from "react";

import { getFilterOptions } from "../api/usersApi";

type FilterProps = {
  label: string;
  value: string;
  options?: string[];
  fetchTerm?: string;
  fetchOptions?: (url: string) => Promise<string[]>;
  onChange: (value: string) => void;
};

export default function Filter({
  label,
  value,
  options = [],
  fetchTerm,
  fetchOptions = getFilterOptions,
  onChange,
}: FilterProps) {
  const [loadedOptions, setLoadedOptions] = useState<string[]>(options);
  const [searchValue, setSearchValue] = useState("");
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if (!fetchTerm || !fetchOptions) {
      return;
    }

    const loadOptions = async () => {
      try {
        const data = await fetchOptions(fetchTerm);
        setLoadedOptions(data);
      } catch (error) {
        console.error("Failed to load filter options:", error);
      }
    };

    loadOptions();
  }, [fetchTerm, fetchOptions]);

  const filteredOptions = loadedOptions.filter((option) =>
    option.toLowerCase().includes(searchValue.toLowerCase()),
  );

  const handleSelect = (option: string) => {
    onChange(option);
    setSearchValue("");
    setIsOpen(false);
  };

  return (
    <div className="relative">
      <label className="mb-1 block text-sm font-medium text-gray-700">
        {label}
      </label>

      <button
        type="button"
        onClick={() => setIsOpen((open) => !open)}
        className="flex w-full min-w-40 items-center justify-between rounded-md border border-gray-300 bg-white px-3 py-2 text-left text-sm text-gray-700 shadow-sm hover:bg-gray-50"
      >
        <span>{value || "All"}</span>
        <span className="ml-2 text-gray-400">{isOpen ? "▲" : "▼"}</span>
      </button>

      {isOpen && (
        <div className="absolute left-0 z-20 mt-1 w-full min-w-40 overflow-hidden rounded-md border border-gray-300 bg-white shadow-lg">
          <div className="border-b border-gray-200 p-2">
            <input
              type="text"
              value={searchValue}
              onChange={(event) => setSearchValue(event.target.value)}
              placeholder={`Search ${label.toLowerCase()}...`}
              autoFocus
              className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-gray-500 focus:ring-1 focus:ring-gray-500"
            />
          </div>

          <div className="max-h-60 overflow-y-auto py-1">
            <button
              type="button"
              onClick={() => handleSelect("")}
              className="block w-full px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
            >
              All
            </button>

            {filteredOptions.length > 0 ? (
              filteredOptions.map((option) => (
                <button
                  key={option}
                  type="button"
                  onClick={() => handleSelect(option)}
                  className={`block w-full px-3 py-2 text-left text-sm hover:bg-gray-100 ${
                    option === value
                      ? "bg-gray-100 font-medium text-gray-900"
                      : "text-gray-700"
                  }`}
                >
                  {option}
                </button>
              ))
            ) : (
              <div className="px-3 py-2 text-sm text-gray-500">
                No results found
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
