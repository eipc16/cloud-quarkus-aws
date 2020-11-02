import axios, {AxiosResponse} from 'axios';

export interface ImageDetails {
    imageId: string | null;
    name: string;
    shortDescription: string;
    file: File | null;
}

export interface FileDetails {
    objectKey: string;
    bucketName: string;
}

export interface ImageResponse {
    id: number;
    name: string;
    shortDescription: string;
    lastUpdated: Date;
    fileInfo: FileDetails;
}

export async function uploadImage(image: ImageDetails) {
    const file = image.file;
    if (file == null)
        return;
    var formData = new FormData();
    formData.append("file", file as Blob);
    formData.append("fileName", file.name);
    formData.append("mimeType", file.type);
    formData.append("name", image.name);
    formData.append("shortDescription", image.shortDescription);

    let response: ImageResponse | null = null;

    if (image.imageId == null || image.imageId !== '') {
        response = await axios.put(`http://localhost:8080/images/${image.imageId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((response: AxiosResponse<ImageResponse>) => response.data);
    } else {
        response = await axios.post('http://localhost:8080/images', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((response: AxiosResponse<ImageResponse>) => response.data);
    }

    return response;
}

export function getImageById(imageId: number) {
    return axios.get(`http://localhost:8080/images/${imageId}`)
        .then((response: AxiosResponse<ImageResponse>) => response.data);
}

export function deleteImageById(imageId: number) {
    return axios.delete(`http://localhost:8080/images/${imageId}`)
}