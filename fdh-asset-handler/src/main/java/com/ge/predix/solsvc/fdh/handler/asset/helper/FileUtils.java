/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.ge.predix.entity.moduleconfigroot.ModuleConfigRoot;

/**
 * This class provides public utilities for accessing and reading file on the
 * file system.
 */
@SuppressWarnings("nls")
public class FileUtils
{
    private static Logger      logger         = LoggerFactory.getLogger(FileUtils.class);

    /**
     * line separator independent of the underlining OS
     */
    public static final String NL             = System.getProperty("line.separator");

    /**
     * file separator independent of the underlinding OS
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /**
     * 
     */
    public static final String TMP            = "tmp";

    /**
     * Utility classes should not have a public or default constructor.
     */
    private FileUtils()
    {

    }

    /**
     * Read a text file
     * 
     * @param fileURL
     *            the filename (full path) of the file to be read
     * @return the text from the file as a String
     * @throws IOException
     *             if an error occurs while reading
     */
    public static String readFile(String fileURL)
            throws IOException
    {

        InputStream is = null;

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileURL);

        if ( is == null )
        {
            is = new FileInputStream(new File(fileURL));
        }

        BufferedReader fr = new BufferedReader(new InputStreamReader(is));

        String text = null;

        StringBuffer sb = new StringBuffer("");

        try
        {

            int bufsize = 1024;
            char[] buf = new char[bufsize];
            buf[0] = '\0';

            int len = -1;
            int off = 0;
            while ((len = fr.read(buf, off, bufsize)) != -1)
            {
                sb.append(buf, off, len);
                buf[0] = '\0';
            }

            text = sb.toString();

        }
        finally
        {
            if ( fr != null )
            {
                fr.close();
            }
        }
        return text;
    }

    /**
     * Read a text file
     * 
     * @param inputStream
     *            the inputStream of the file to be read
     * @return the text from the file as a String
     * @throws IOException
     *             if an error occurs while reading
     */
    public static String readFile(InputStream inputStream)
            throws IOException
    {

        BufferedReader fr = new BufferedReader(new InputStreamReader(inputStream));

        String text = null;

        StringBuffer sb = new StringBuffer("");
        try
        {

            int bufsize = 1024;
            char[] buf = new char[bufsize];
            buf[0] = '\0';

            int len = -1;
            int off = 0;
            while ((len = fr.read(buf, off, bufsize)) != -1)
            {
                sb.append(buf, off, len);
                buf[0] = '\0';
            }

            text = sb.toString();

        }
        finally
        {
            if ( fr != null )
            {
                fr.close();
            }
        }
        return text;
    }

    /**
     * Write a text file
     * 
     * @param filename
     *            the filename (full path) to be written to
     * @param text
     *            the text to be written
     * @param create
     *            if true, the directory and any sub-directories where the file
     *            resides are created, otherwise the directory must exist first
     * @throws IOException
     *             if an error occurs while writing to the file
     */
    public static void writeFile(String filename, String text, boolean create)
            throws IOException
    {
        FileWriter fw = null;
        PrintWriter pw = null;

        if ( create )
        {
            createDirectoriesForFile(filename);
        }

        try
        {
            fw = new FileWriter(filename);
            pw = new PrintWriter(fw);

            pw.print(text);

        }
        finally
        {
            if ( fw != null )
            {
                fw.close();
            }
        }
    }

    /**
     * Write a text file
     * 
     * @param filename
     *            the filename (full path) to be written to
     * @param text
     *            the text to be written
     * @param create
     *            if true, the directory and any sub-directories where the file
     *            resides are created, otherwise the directory must exist first
     * @param append
     *            if true, the text will be written to the end of the file
     * @throws IOException
     *             if an error occurs while writing to the file
     */
    public static void writeFile(String filename, String text, boolean create, boolean append)
            throws IOException
    {
        PrintWriter pw = null;

        if ( create )
        {
            createDirectoriesForFile(filename);
        }

        try
        {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, append)));
            pw.print(text);

        }
        finally
        {
            if ( pw != null )
            {
                pw.close();
            }
        }
    }

    /**
     * creates, the directory and any sub-directories where the file resides are
     * created
     * 
     * @param filename
     *            the filename (full path) to where to create directories
     */
    public static void createDirectoriesForFile(String filename)
    {
        List<File> dirs = new ArrayList<File>();
        File f = new File(filename);

        // Determine which directories don't yet exist
        File fDir = f.getParentFile();
        while (!fDir.exists())
        {
            dirs.add(fDir);
            fDir = fDir.getParentFile();
            if ( fDir == null )
            {
                break;
            }
        }

        // Create directories in order
        for (int i = dirs.size(); i > 0; i--)
        {
            File ff = dirs.get(i - 1);
            ff.mkdir();
        }

    }

    /**
     * compares two files if they are equal
     * 
     * @param resultsFile
     *            the filename (full path) to be compared
     * @param expectedResultsFile
     *            the filename (full path) to be compared
     * @return true if filename1 equals filename2
     * @throws IOException -
     */
    public static boolean compare2files(String resultsFile, String expectedResultsFile)
            throws IOException
    {

        String s1 = null;
        String s2 = null;

        s1 = FileUtils.readFile(resultsFile);
        s2 = FileUtils.readFile(expectedResultsFile);

        // to handle the unix cr lf vs windows lf issue
        s1 = s1.replaceAll("\\r\\n", "\n");
        s2 = s2.replaceAll("\\r\\n", "\n");

        return compare2Strings(s1, s2);

    }

    /**
     * compares the content of two files and returns the point of mismatch
     * 
     * @param controlFilePath
     *            the filename that acts as Control (Good Data) to be compared
     * @param testFilePath
     *            the filename (full path) to be compared
     * @return returnData Returns null when both control and test files are same
     *         or the line content where the two files mismatch
     * @throws IOException -
     */
    @SuppressWarnings("resource")
    public static String compareTwoFilesByLine(String controlFilePath, String testFilePath)
            throws IOException
    {

        File controlFile = new File(controlFilePath);
        File testFile = new File(testFilePath);

        String controlData = null;
        String testData = null;
        String returnData = null;
        BufferedReader controlLineBuffer = new BufferedReader(new FileReader(controlFile));
        BufferedReader testLineLineBuffer = new BufferedReader(new FileReader(testFile));

        while ((controlData = controlLineBuffer.readLine()) != null
                && (testData = testLineLineBuffer.readLine()) != null)
        {
            if ( !controlData.equals(testData) )
            {
                returnData = " Files " + controlFilePath + " and " + testFilePath + "are different at Line :  "
                        + controlData + "And Line : " + testData + "respectively. ";
                return returnData;
            }
        }

        if ( !(controlData == null && (testData = testLineLineBuffer.readLine()) == null) )
        {

            returnData = " :Files " + controlFilePath + " and " + testFilePath + "are different at Line :  "
                    + controlData + " And Line : " + testData + " respectively. ";
            return returnData;
        }
        return returnData;
    }

    /**
     * compare a result String to the contents of the expected result file.
     * Return false if they do not match and write the explanation of where the
     * match did not occur in the tmp directory that is relative to the run's
     * working directory.
     * 
     * @param resultString -
     * @param expectedResultFile -
     * @return -
     * @throws IOException -
     */
    public static boolean compareStringAndFile(String resultString, String expectedResultFile)
            throws IOException
    {
        try
        {
            String expectedResultString = null;
            expectedResultString = FileUtils.readFile(expectedResultFile);

            if ( expectedResultString == null )
            {
                expectedResultString = "";
            }
            if ( resultString == null )
            {
                resultString = "";
            }

            // ReplaceAll()s added to handle the unix cr lf vs windows lf issue
            boolean status = compare2Strings(resultString.replaceAll("\\r\\n", "\n"),
                    expectedResultString.replaceAll("\\r\\n", "\n"));

            return status;
        }
        catch (IOException e)
        {
            try
            {
                FileUtils.writeFile(TMP + File.separatorChar + "testOutput.txt", resultString, true);

                String explanation = e.getMessage();
                FileUtils.writeFile(TMP + File.separatorChar + "explanation.txt", explanation, true);

                return false;
            }
            catch (IOException e1)
            {
                logger.error("Failed to write error message", e1);
                throw e1;
            }
        }

    }

    /**
     * compare a result String to the contents of the expected result file.
     * Return true if each line in the expected result file is contained in
     * result string. Order doesn't matter.
     * 
     * @param inResultString
     *            string produce by a test
     * @param expectedResultFile
     *            expected result saved in file
     * @return true if comparison was successful
     * @throws IOException
     *             Throws IOException
     */
    public static boolean compareStringAndFileContentUnordered(String inResultString, String expectedResultFile)
            throws IOException
    {
        String resultString = inResultString;
        try
        {
            String expectedResultString = null;

            expectedResultString = FileUtils.readFile(expectedResultFile);

            if ( expectedResultString == null )
            {
                expectedResultString = "";
            }
            if ( resultString == null )
            {
                resultString = "";
            }

            // ReplaceAll()s added to handle the unix cr lf vs windows lf issue
            boolean status = compare2StringsLineByLineUnordered(resultString.replaceAll("\\r\\n", "\n"),
                    expectedResultString.replaceAll("\\r\\n", "\n"));

            return status;
        }
        catch (IOException e)
        {
            try
            {
                FileUtils.writeFile(TMP + File.separatorChar + "testOutput.txt", resultString, true);

                String explanation = e.getMessage();
                FileUtils.writeFile(TMP + File.separatorChar + "explanation.txt", explanation, true);

                return false;
            }
            catch (IOException e1)
            {
                logger.error("Failed to write error message", e1);
                throw e1;
            }
        }
    }

    private static boolean compare2StringsLineByLineUnordered(String s1, String s2)
            throws IOException
    {

        if ( s1 == null || s2 == null ) return false;

        // Check that each line from s1 is in s2
        BufferedReader s2Reader = new BufferedReader(new StringReader(s2));
        String s2Line = null;
        while ((s2Line = s2Reader.readLine()) != null)
        {
            BufferedReader s1Reader = new BufferedReader(new StringReader(s1));
            boolean found = false;
            String s1Line = null;
            while ((s1Line = s1Reader.readLine()) != null)
            {
                if ( s1Line.equals(s2Line) )
                {
                    found = true;
                    break;
                }
            }
            if ( !found )
            {
                int diffStart = s2.indexOf(s2Line);
                writeFailureExplanation(s2, s1, diffStart);
                return false;
            }
        }
        return true;
    }

    private static boolean compare2Strings(String s1, String s2)
            throws IOException
    {

        if ( s1 != null && s2 != null )
        {
            boolean status = s1.equals(s2);

            if ( !status )
            {
                int diffStart = -1;
                for (int i = 0; i < s1.length(); i++)
                {
                    if ( i >= s2.length() )
                    {
                        diffStart = i;
                        break;
                    }

                    if ( s1.charAt(i) != s2.charAt(i) )
                    {
                        diffStart = i;
                        break;
                    }
                }
                writeFailureExplanation(s1, s2, diffStart);
                return false;
            }

        }
        return true;
    }

    /**
     * Compare a string to the contents of a file
     * 
     * @param result -
     * @param expecteResultsFile -
     * @param parser -
     * @return -
     * @throws IOException -
     */
    public static boolean verifyOSAResult(String result, String expecteResultsFile, IOsacbmXMLParsing parser)
            throws IOException
    {

        return verifyOSAResult(result, expecteResultsFile, parser, false, false);
    }

    /**
     * Compare a string to the contents of a file
     * 
     * @param result -
     * @param expecteResultsFile -
     * @param parser -
     * @param ignoreCase -
     * @param ingoreCrlf -
     * @return -
     * @throws IOException -
     */
    public static boolean verifyOSAResult(String result, String expecteResultsFile, IOsacbmXMLParsing parser,
            boolean ignoreCase, boolean ingoreCrlf)
                    throws IOException
    {
        boolean status = false;

        ModuleConfigRoot testResult = parser.unmarshal(result);

        String expectedResult = null;

        try
        {

            // if the test fails uncomment out the next line to write the
            // temporary file to lastTestOutput/testResults.txt if the string to
            // too long to interrogate in the buffer

            expectedResult = FileUtils.readFile(expecteResultsFile);

            if ( expectedResult == null )
            {
                // gold standard file couldn't not be found or there was a
                // problem reading it
                return false;
            }

            ModuleConfigRoot goldCaseResult = parser.unmarshal(expectedResult);

            try
            {
                status = ObjectCompare.areObjectsCongruent(testResult, goldCaseResult, ignoreCase, ingoreCrlf);
            }
            catch (IllegalArgumentException e)
            {
                return false;
            }
            catch (IntrospectionException e)
            {
                return false;
            }
            catch (IllegalAccessException e)
            {
                return false;
            }
            catch (InvocationTargetException e)
            {
                return false;
            }

            if ( !status )
            {
                writeFailureExplanation(result, expectedResult, -1);
            }

            return status;
        }
        catch (IOException e)
        {
            FileUtils.writeFile(TMP + File.separatorChar + "testOutput.xml", result, true);
            throw e;
        }

    }

    /**
     * 
     * @param result -
     * @param expectedResults -
     * @param atChar
     *            the point where the differences started
     * @throws IOException -
     */
    public static void writeFailureExplanation(String result, String expectedResults, int atChar)
            throws IOException
    {
        String explanation = null;
        if ( expectedResults != null && !expectedResults.isEmpty() )
        {
            if ( atChar == -1 )
            {
                explanation = "The 2 objects have different contents from the very beginning, see the tmp/testOutput.xml and tmp/expectedResults.txt files.";
            }
            else
            {
                int resultStart = Math.max(atChar - 20, 0);
                int resultEnd = Math.min(atChar + 20, result.length() - 1);
                int expectedStart = Math.max(atChar - 20, 0);
                int expectedEnd = Math.min(atChar + 20, expectedResults.length() - 1);
                explanation = "The 2 objects have different contents, the differences start at character " + atChar
                        + " with the results containing: \"" + result.substring(resultStart, resultEnd) + "\""
                        + System.getProperty("line.separator") + " and the expected results containing: \""
                        + expectedResults.substring(expectedStart, expectedEnd) + "\"";
            }
        }
        else
        {
            explanation = "Expected results file was not found, or is empty.";
        }

        FileUtils.writeFile(TMP + File.separatorChar + "testOutput.xml", result, true);
        FileUtils.writeFile(TMP + File.separatorChar + "explanation.txt", explanation, true);
        if ( expectedResults != null && !expectedResults.isEmpty() )
        {
            FileUtils.writeFile(TMP + File.separatorChar + "expectedResults.txt", expectedResults, true);
        }
    }

    /**
     * given a filename or url find its URI
     * 
     * @param fileURL -
     * @param context -
     * @return -
     */
    public static URI getURI(String fileURL, ApplicationContext context)
    {
        URI outputFileURI = null;
        if ( context != null )
        {
            try
            {
                Resource resource = context.getResource(fileURL);
                outputFileURI = resource.getURI();
            }
            catch (IOException e)
            {
                logger.error("Error while fetching resource {}", fileURL, e);
                return null;
            }
        }
        else
        {
            try
            {
                URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(fileURL);
                if ( resourceURL == null )
                {
                    outputFileURI = new URI(fileURL);
                }
                else
                {
                    outputFileURI = resourceURL.toURI();
                }
            }
            catch (URISyntaxException e)
            {
                logger.error("Error while fetching resource {}", fileURL, e);
                return null;
            }
        }

        return outputFileURI;

    }

    /**
     * @param path -
     * @return -
     */
    public static String toJavaPath(String path)
    {
        String javaPath = null;
        if ( path != null )
        {
            javaPath = path.replace("\\", "/");
        }
        return javaPath;
    }

}
