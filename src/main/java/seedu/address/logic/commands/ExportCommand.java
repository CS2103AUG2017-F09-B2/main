package seedu.address.logic.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.place.ReadOnlyPlace;
import seedu.address.model.tag.Tag;

/**
 * Exports the current Tourist Book data into a CSV file.
 */
public class ExportCommand extends Command {
    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_EXPORT_SUCCESS = "Successfully exported data.";

    public static final String MESSAGE_EMPTY_ADDRESS_BOOK = "Export failed. Current Tourist-book is empty.";

    public static final String DATA_FILE_PATH = "TouristBookData.csv";

    private List<ReadOnlyPlace> currentData;

    @Override
    public CommandResult execute() throws CommandException {
        this.currentData = model.getAddressBook().getPlaceList();

        if (currentData.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_ADDRESS_BOOK);
        }

        if (fileExists()) {
            deleteFile();
        }

        createFile();
        writeData();
        return new CommandResult(MESSAGE_EXPORT_SUCCESS);
    }

    /** Constructing the headers for CSV */
    private String constructHeaders() {
        ReadOnlyPlace samplePlace = currentData.get(0);
        List<String> propertyNames = samplePlace.getPropertyNamesAsList();

        final StringBuilder builder = new StringBuilder();

        for (String field : propertyNames) {
            builder.append(field);
            builder.append(",");
        }
        builder.append("Tags");
        builder.append("\n");

        return builder.toString();
    }

    /** Function to write data to the CSV */
    private void writeData() throws CommandException {
        final StringBuilder builder = new StringBuilder();

        File csvData = new File(DATA_FILE_PATH);
        String headers = constructHeaders();

        for (ReadOnlyPlace place : currentData) {
            String placeData = generatePlaceData(place);
            builder.append(placeData);
        }

        String dataToWrite = builder.toString();

        try {
            PrintWriter pw = new PrintWriter(csvData);
            pw.write(headers);
            pw.write(dataToWrite);
            pw.close();
        } catch (IOException ioe) {
            throw new CommandException(ioe.getMessage());
        }

    }

    /**
     * Function to generate Place data as text.
     * Mostly hardcoded for now, until a better implementation can be found.
     */
    private String generatePlaceData(ReadOnlyPlace place) {
        String name = place.getName().toString();
        String phone = place.getPhone().toString();
        String address = place.getAddress().toString();
        address = address.replace(',', ';');
        String postalcode = place.getPostalCode().toString();
        String website = place.getWebsite().toString();
        String tags = parseTagsToString(place);

        final StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(",");
        builder.append(phone);
        builder.append(",");
        builder.append(address);
        builder.append(",");
        builder.append(postalcode);
        builder.append(",");
        builder.append(website);
        builder.append(",");
        builder.append(tags);
        builder.append("\n");

        return builder.toString();
    }

    /** Function to convert tags into a suitable String format for CSV */
    private String parseTagsToString(ReadOnlyPlace place) {
        Set<Tag> tags = place.getTags();
        final StringBuilder builder = new StringBuilder();
        for (Tag tag : tags) {
            String convertedTag = tag.toString();
            builder.append(convertedTag);
        }

        return builder.toString();
    }

    /** Function to create the CSV */
    private void createFile() throws CommandException {
        try {
            File csvData = new File(DATA_FILE_PATH);
            csvData.createNewFile();
        } catch (IOException ioe) {
            throw new CommandException(ioe.getMessage());
        }
    }

    private boolean fileExists() {
        File csvData = new File(DATA_FILE_PATH);
        return csvData.exists();
    }

    private void deleteFile() {
        File csvData = new File(DATA_FILE_PATH);
        csvData.delete();
    }
}
