type SearchUsersProps = {
  value: string;
  searchButtonDisabled: boolean;
  onChange: (value: string) => void;
  onSearch: () => void;
};

export default function SearchUsers({
  value,
  onChange,
  onSearch,
  searchButtonDisabled: searchDisabled,
}: SearchUsersProps) {
  return (
    <div className="flex w-full max-w-md gap-2">
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter" && !searchDisabled) {
            onSearch();
          }
        }}
        placeholder="Search users..."
        className="w-full rounded-md border border-gray-300 px-4 py-2 text-sm
                   outline-none focus:border-blue-600 focus:border-2 focus:blue-1 focus:ring-blue-600"
      />

      <button
        disabled={searchDisabled}
        type="button"
        onClick={onSearch}
        className="rounded-md bg-gray-900 px-4 py-2 text-sm font-medium
                   text-white hover:bg-gray-700 disabled:bg-gray-500"
      >
        Search
      </button>
    </div>
  );
}
