package com.quarkdata.yunpan.api.util;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class XferMgrProgress
{
    // waits for the transfer to complete, catching any exceptions that occur.
    public static void waitForCompletion(Transfer xfer)
    {
        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service error: " + e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            System.err.println("Amazon client error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Transfer interrupted: " + e.getMessage());
            System.exit(1);
        }
    }

    // Prints progress while waiting for the transfer to finish.
    public static void showTransferProgress(Transfer xfer)
    {
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(666);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
//            long so_far = progress.getBytesTransferred();
//            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            printProgressBar(pct);
        } while (xfer.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = xfer.getState();
        System.out.println(": " + xfer_state);
    }

    // Prints progress of a multiple file upload while waiting for it to finish.
    public static void showMultiUploadProgress(MultipleFileUpload multi_upload)
    {
        // print the upload's human-readable description
        System.out.println(multi_upload.getDescription());

        Collection<? extends Upload> sub_xfers = new ArrayList<Upload>();
        sub_xfers = multi_upload.getSubTransfers();

        do {
            System.out.println("\nSubtransfer progress:\n");
            for (Upload u : sub_xfers) {
                System.out.println("  " + u.getDescription());
                if (u.isDone()) {
                    TransferState xfer_state = u.getState();
                    System.out.println("  " + xfer_state);
                } else {
                    TransferProgress progress = u.getProgress();
                    double pct = progress.getPercentTransferred();
                    printProgressBar(pct);
                    System.out.println();
                }
            }

            // wait a bit before the next update.
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
               return;
            }
        } while (multi_upload.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = multi_upload.getState();
        System.out.println("\nMultipleFileUpload " + xfer_state);
    }

    public static void printProgressBar(double pct)
    {	
    	DecimalFormat df = new DecimalFormat("#.00");
        System.out.println(df.format(pct));
    }


    public static void uploadFileWithListener(String file_path,
          String bucket_name, String key_prefix, boolean pause)
    {
        System.out.println("file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }

        File f = new File(file_path);
        TransferManager xfer_mgr = new TransferManager();
        try {
            Upload u = xfer_mgr.upload(bucket_name, key_name, f);
            // print an empty progress bar...
            printProgressBar(0.0);
            u.addProgressListener(new ProgressListener() {
                public void progressChanged(ProgressEvent e) {
                    double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                    printProgressBar(pct);
                }
            });
            // block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(u);
            // print the final state of the transfer.
            TransferState xfer_state = u.getState();
            System.out.println(": " + xfer_state);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void uploadDirWithSubprogress(String dir_path,
            String bucket_name, String key_prefix, boolean recursive,
            boolean pause)
    {
        System.out.println("directory: " + dir_path + (recursive ?
                    " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager();
        try {
            MultipleFileUpload multi_upload = xfer_mgr.uploadDirectory(
                    bucket_name, key_prefix, new File(dir_path), recursive);
            // loop with Transfer.isDone()
            XferMgrProgress.showMultiUploadProgress(multi_upload);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(multi_upload);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

}
