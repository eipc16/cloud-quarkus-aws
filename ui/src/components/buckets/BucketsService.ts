import axios, { AxiosResponse } from 'axios';

export interface Bucket {
    bucketName: string;
    createDate: Date;
}

export async function fetchBuckets() {
    const result: Bucket[] = await axios('http://localhost:8080/buckets')
        .then((response: AxiosResponse<Bucket[]>) => response.data);
    return result;
}

export interface FileObject {
    objectKey: string;
    size: number;
}

export async function fetchFiles(bucket: Bucket) {
    const result: FileObject[] = await axios(`http://localhost:8080/buckets/${bucket.bucketName}`)
        .then((response: AxiosResponse<FileObject[]>) => response.data);
    return result;
}

export function downloadFile(bucketName: string, file: FileObject) {
    axios({
        url: getFileUrl(bucketName, file.objectKey),
        method: 'GET',
        responseType: 'blob'
    }).then((response: AxiosResponse<any>) => {
        const filename = file.objectKey.replace(/^.*[\\/]/, '');
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    })
}

export function getFileUrl(bucketName: String, objectKey: string) {
    return `http://localhost:8080/buckets/${bucketName}/files/${objectKey}`
}

export async function uploadFile(bucketName:string, file: File) {
    var formData = new FormData();

    formData.append("file", file as Blob);
    formData.append("fileName", file.name);
    formData.append("mimeType", file.type);

    await axios.post(`http://localhost:8080/buckets/${bucketName}/files`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}