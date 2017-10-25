package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_IMAGE;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.commands.PhotoCommand.MESSAGE_PHOTO_PERSON_SUCCESS;
import static seedu.address.testutil.TestUtil.getPerson;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PhotoCommand;
import seedu.address.logic.parser.exceptions.ImageException;
import seedu.address.model.Model;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;



public class PhotoCommandSystemTest extends AddressBookSystemTest {

    private static final String MESSAGE_INVALID_PHOTO_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, PhotoCommand.MESSAGE_USAGE);

    @Test
    public void addPhotoTests() throws IOException {
        /* ----------------- Performing photo operation while an unfiltered list is being shown -------------------- */

        /* Case: Add photo to the first person in the list, command with leading spaces and
          trailing spaces -> deleted */

        //Find the relative path of a file.

        String path = "/Users/ronaklakhotia/Desktop/Ronak.jpeg";
        String base = "/Users/ronaklakhotia/Desktop";
        String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();

        File fileToRead = null;
        BufferedImage image = null;

        File fileToWrite = null;

        String uniquePath = null;
        //System.out.println(relative);

        try {

            String url = path + "";
            image = new BufferedImage(963, 640, BufferedImage.TYPE_INT_ARGB);
            fileToRead = new File(url);
            image = ImageIO.read(fileToRead);
            uniquePath = Integer.toString(path.hashCode());
            fileToWrite = new File("src/main/resources/images/" + uniquePath + ".jpg");
            ImageIO.write(image, "jpg", fileToWrite);


        } catch (IOException e) {
            throw  new ImageException(String.format(MESSAGE_INVALID_IMAGE,
                    PhotoCommand.MESSAGE_FILE_PATH_NOT_FOUND));
        }


        Model expectedModel = getModel();
        String command = "     " + PhotoCommand.COMMAND_WORD + "      " + INDEX_FIRST_PERSON.getOneBased()
                + "       " + "src/main/resources/images/" + uniquePath + ".jpg" + "   ";

        String FilePath = "src/main/resources/images/" + uniquePath + ".jpg";

        ReadOnlyPerson photoPerson = getTargetPerson(expectedModel, INDEX_FIRST_PERSON, FilePath);
        String expectedResultMessage = String.format(MESSAGE_PHOTO_PERSON_SUCCESS, photoPerson);
        assertCommandSuccess(command, expectedModel, expectedResultMessage);


        /* --------------------------------- Performing invalid photo operation ------------------------------------ */

        /* Case: invalid index (0) -> rejected */
        command = PhotoCommand.COMMAND_WORD + " 0 " + FilePath;
        assertCommandFailure(command, MESSAGE_INVALID_PHOTO_COMMAND_FORMAT);

        /* Case: invalid index (-1) -> rejected */
        command = PhotoCommand.COMMAND_WORD + " -1" + FilePath;
        assertCommandFailure(command, MESSAGE_INVALID_PHOTO_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getAddressBook().getPersonList().size() + 1);
        command = PhotoCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased() + " " + FilePath;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(PhotoCommand.COMMAND_WORD
                + " abc " + FilePath, MESSAGE_INVALID_PHOTO_COMMAND_FORMAT);

        /* Case: invalid arguments (extra argument) -> rejected */
        assertCommandFailure(PhotoCommand.COMMAND_WORD + " 1 abc "
                + FilePath, MESSAGE_INVALID_PHOTO_COMMAND_FORMAT);

    }
    @Test
    public void checkForInvalidPath() {

        /* Case When incorrect path is entered */
        String command =  PhotoCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased()
                + " " + "src/main/resources/images/" + "random" + ".jpg";

        assertCommandFailure(command, MESSAGE_INVALID_IMAGE);

    }

    /**
     * Adds photo to the {@code ReadOnlyPerson} at the specified {@code index} in {@code model}'s address book.
     * @return the person with the photo added
     */
    private ReadOnlyPerson getTargetPerson(Model model, Index index, String FilePath) {
        ReadOnlyPerson targetPerson = getPerson(model, index);
        try {
            model.addPhotoPerson(targetPerson, FilePath, index);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("targetPerson is retrieved from model.");
        } catch (IOException ioe) {
            throw new AssertionError("Illegal values entered");
        }
        return targetPerson;
    }
    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }

    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String)} except that the browser url
     * and selected card are expected to update accordingly depending on the card at {@code expectedSelectedCardIndex}.
     * @see PhotoCommandSystemTest#assertCommandSuccess(String, Model, String)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
                                      Index expectedSelectedCardIndex) {
        executeCommand(command);
        System.out.println(expectedResultMessage);
        System.out.println(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);

        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }


        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }


}

