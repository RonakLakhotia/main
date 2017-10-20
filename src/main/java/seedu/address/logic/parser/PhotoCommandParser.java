package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_IMAGE;

import java.io.File;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.PhotoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new PhotoCommand object
 */
public class PhotoCommandParser implements Parser<PhotoCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PhotoCommand
     * and returns an PhotoCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public PhotoCommand parse(String args) throws ParseException {

        String trimmedArgs = args.trim();
        String[] Keywords = trimmedArgs.split("\\s+");

        if (trimmedArgs.isEmpty() || Keywords.length != 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, PhotoCommand.MESSAGE_USAGE));
        }
        String home = System.getProperty("user.home");
        String inputFile = Keywords[1];
        java.nio.file.Path path = java.nio.file.Paths.get(home, "Desktop");
        String url = path + "";
        System.out.println(path + " " + inputFile);
        File workingDirectory = new File(url);
        File testFile = new File(workingDirectory, inputFile);
        if (!testFile.exists() && !inputFile.equalsIgnoreCase("delete")) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_IMAGE, PhotoCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(Keywords[0]);
            return new PhotoCommand(index, (Keywords[1]));
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, PhotoCommand.MESSAGE_USAGE));
        }
    }
    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
