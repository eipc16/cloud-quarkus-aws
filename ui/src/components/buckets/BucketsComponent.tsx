import React, {useEffect, useState} from 'react';
import Typography from '@material-ui/core/Typography';
import MenuList from '@material-ui/core/MenuList';
import MenuItem from '@material-ui/core/MenuItem';
import Button from '@material-ui/core/Button'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import {Bucket, downloadFile, fetchBuckets, fetchFiles, FileObject, uploadFile} from "./BucketsService";

import './BucketsComponent.scss';

interface ListItemProps {
    bucketName: string;
    file: FileObject;
}

const FileListColumn: React.FC<{}> = () => (
    <ListItem className='file--list--element'>
        <p className='file--objectkey file--list--column'>Object Key</p>
        <p className='file--size file--list--column'>Size (Kb)</p>
        <p className='file--download file--list--column'>Download Button</p>
    </ListItem>
);

const FileListItem: React.FC<ListItemProps> = ({bucketName, file}) => {

    const formatBytes = (bytes: number) => {
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        if(bytes === 0)
            return `0 ${sizes[0]}`;
        const size_bucket = Math.floor(Math.log(bytes) / Math.log(1024));
        const size = Math.round((bytes / Math.pow(1024, size_bucket)) * 100) / 100;
        return `${size} ${sizes[size_bucket]}`;
    };

    return (
        <ListItem className='file--list--element'>
            <p className='file--objectkey'>{file.objectKey}</p>
            <p className='file--size'>{formatBytes(file.size)}</p>
            <Button className='file--download' variant="contained" color="primary"
                    onClick={() => downloadFile(bucketName, file)}>
                Download
            </Button>
        </ListItem>
    )
};

const BucketDetailsView: React.FC<{bucket: Bucket}> = ({bucket}) => {
    const [files, setBucketFiles] = useState<FileObject[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const fileField = React.useRef<HTMLInputElement>(null);

    const handleFileSelected = (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if(files && files.length > 0) {
            uploadFile(bucket.bucketName, files[0])
                .then(() => {
                    loadFiles();
                })
        }
    };

    useEffect(() => {
        loadFiles();
    }, [bucket]);

    const loadFiles = () => {
        setIsLoading(true);
        fetchFiles(bucket).then(files => {
            setBucketFiles(files);
            setIsLoading(false);
        });
    };

    return (
        <div className='bucket--details'>
            <div className='bucket--upload'>
                <input id="file--upload--input"
                       type="file"
                       ref={fileField}
                       onChange={handleFileSelected}
                       style={{visibility: 'hidden'}} />
                <Button
                    className='file--upload--button'
                    onClick={() => {
                        const inputElement = document.getElementById('file--upload--input');
                        if(inputElement) {
                            inputElement.click();
                        }
                    }}
                >
                    Upload
                </Button>
            </div>
            <div className='bucket--files'>
                {
                    files.length > 0 ? (
                        <List>
                            <FileListColumn />
                            {
                                files.map((file, index) =>
                                    <FileListItem key={index} bucketName={bucket.bucketName} file={file}/>
                                )
                            }
                        </List>
                    ) : (
                        isLoading ? (
                            <p>Loading files for bucket: {bucket.bucketName}</p>
                        ) : (
                            <p>No files in bucket: {bucket.bucketName}</p>
                        )
                    )
                }
            </div>
        </div>
    )
};

interface BucketListProps {
    buckets: Bucket[],
    selectedBucket: Bucket | null,
    onBucketSelected: (bucket: Bucket) => void;
}

const BucketListView: React.FC<BucketListProps> = ({buckets, selectedBucket, onBucketSelected}) => (
    <MenuList key='bucket--list'>
        {
            buckets.map((bucket, index) => (
               <MenuItem key={index}
                         button
                         selected={selectedBucket != null && bucket.bucketName === selectedBucket.bucketName}
                         onClick={() => onBucketSelected(bucket)}>
                   <Typography variant='inherit'>{bucket.bucketName}</Typography>
               </MenuItem>
            ))
        }
    </MenuList>
);

export const BucketsComponent: React.FC<{}> = () => {
    const [selectedBucket, setSelectedBucket] = useState<Bucket | null>(null);
    const [bucketList, setBucketList] = useState<Bucket[]>([]);

    useEffect( () => {
        fetchBuckets().then(data => {
            setBucketList(data);
            if(data.length > 0) {
                setSelectedBucket(data[0]);
            }
        });
    }, []);

    return (
        <div className='bucket--component'>
            <div className='buckets--menu'>
                <BucketListView buckets={bucketList} 
                                selectedBucket={selectedBucket} 
                                onBucketSelected={(bucket: Bucket) => setSelectedBucket(bucket)} />
            </div>
            <div className='bucket--details--view'>
                {
                    selectedBucket ? (
                        <BucketDetailsView bucket={selectedBucket}/>
                    ) : (
                        <div>Please select a bucket</div>
                    )
                }
            </div>
        </div>
    )
};