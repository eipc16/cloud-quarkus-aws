import React, {useEffect, useState} from "react";
import {useFormik} from 'formik';
import {
    deleteImageById,
    getImageById,
    ImageDetails,
    ImageResponse,
    LabelDetectionResponse,
    recognizeLabel,
    recognizeTextByImageName,
    TextDetectionResponse,
    uploadImage
} from "./ImagesService";
import TextField from "@material-ui/core/TextField";
import Button from '@material-ui/core/Button'

import './ImagesComponent.scss';
import {getFileUrl} from "../buckets/BucketsService";

const ImagesForm: React.FC<{ onImageUploaded: (image: number) => void }> = ({onImageUploaded}) => {
    const [isSubmitting, setIsSubmitting] = useState(false);

    const formik = useFormik({
        initialValues: {
            imageId: '',
            name: '',
            shortDescription: '',
            file: null
        },
        validate: (values: ImageDetails) => {
            if (values.name === '') {
                return {
                    name: 'Name is required'
                }
            }
            if (values.file == null) {
                return {
                    file: 'File is required'
                }
            }
        },
        onSubmit: (values: ImageDetails) => {
            setIsSubmitting(true);
            uploadImage(values)
                .then((value) => {
                    if (value) {
                        formik.setFieldValue("imageId", value.id);
                        onImageUploaded(value.id);
                    }
                    setIsSubmitting(false);
                });
        }
    });

    const handleFileSelected = (fieldName: string, e: React.ChangeEvent<HTMLInputElement>) => {
        const files = e.target.files;
        if (files && files.length > 0) {
            formik.setFieldValue(fieldName, files[0]);
        }
    };

    return (
        <form onSubmit={formik.handleSubmit} className='image--submit--form'>
            <TextField name="imageId"
                       className='form--input'
                       label="Image Id"
                       onChange={formik.handleChange}
                       placeholder="Leave empty if you want to upload new file"
                       value={formik.values.imageId}/>
            <TextField name="name"
                       className='form--input'
                       label="Name"
                       required={true}
                       error={formik.errors.name != null}
                       onChange={formik.handleChange}
                       value={formik.values.name}/>
            <TextField name="shortDescription"
                       className='form--input'
                       label="Short Description"
                       onChange={formik.handleChange}
                       value={formik.values.shortDescription}/>
            <input id="file"
                   name="file"
                   className='form--input'
                   required={true}
                   type="file"
                   onChange={(event) => handleFileSelected("file", event)}/>
            <Button className='form--input'
                    onClick={() => formik.submitForm()}
                    variant="contained"
                    disabled={isSubmitting}
                    color="primary"
            >
                Upload
            </Button>
            {formik.errors.name && <div>{formik.errors.name}</div>}
            {formik.errors.file && <div>{formik.errors.file}</div>}
        </form>
    )
};

interface ImageDetailsProps {
    currentImageId: number | null;
    onCurrentImageIdChanged: (imageId: number | null) => void;
}

const ImageDetailsComponent: React.FC<ImageDetailsProps> = ({currentImageId, onCurrentImageIdChanged}) => {
    const [currentImage, setCurrentImage] = useState<ImageResponse | null>(null);
    const [detectedTextResponse, setDetectedTextResponse] = useState<TextDetectionResponse | null>(null);
    const [detectedLabelResponse, setDetectedLabelResponse] = useState<LabelDetectionResponse | null>(null);
    const [numberFieldValue, setNumberFieldValue] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isTextRecognitionLoading, setIsTextRecognitionLoading] = useState(false);
    const [isLabelRecognitionLoading, setIsLabelRecognitionLoading] = useState(false);

    useEffect(() => {
        if (currentImageId != null) {
            setIsLoading(true);
            getImageById(currentImageId)
                .then(image => {
                    setCurrentImage(image);
                    setIsLoading(false);
                });
            setNumberFieldValue(currentImageId);
        } else {
            setCurrentImage(null);
            setNumberFieldValue(null);
        }
    }, [currentImageId]);

    const handleSearchBoxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const imageId = parseInt(event.target.value);
        setNumberFieldValue(imageId);
    };

    const handleChangeCurrentId = (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        if (numberFieldValue != null) {
            onCurrentImageIdChanged(numberFieldValue);
            setIsLoading(true);
            getImageById(numberFieldValue)
                .then(image => {
                    setCurrentImage(image);
                    setIsLoading(false);
                });
        }
    };

    const handleTextRecognition = () => {
        if (currentImage != null) {
            setIsTextRecognitionLoading(true);
            recognizeTextByImageName(currentImage.fileInfo.bucketName, currentImage.fileInfo.objectKey).then((res) => {
                setDetectedTextResponse(res);
                setIsTextRecognitionLoading(false);
            });
        }
    };

    const handleLabelRecognition = () => {
        if (currentImage != null) {
            setIsLabelRecognitionLoading(true);
            recognizeLabel(currentImage.fileInfo.bucketName, currentImage.fileInfo.objectKey).then((res) => {
                setDetectedLabelResponse(res);
                setIsLabelRecognitionLoading(false);
            });
        }
    };

    const handleRemoveImage = () => {
        if (currentImage != null) {
            deleteImageById(currentImage.id).then((res) => onCurrentImageIdChanged(null));
        }
    };

    return (
        <div className='image--details--component'>
            <div className='image--search'>
                <TextField className='image--search--element input--id'
                           type='number'
                           label='Image Id'
                           value={numberFieldValue}
                           onChange={handleSearchBoxChange} style={{width: '300px', marginRight: '10px'}}/>
                <Button className='image--search--element'
                        onClick={handleChangeCurrentId}
                        variant="contained"
                        color="primary"
                >
                    Show Image
                </Button>
                <Button className='image--search--element'
                        onClick={handleTextRecognition}
                        variant="contained"
                        disabled={currentImage == null || currentImage.fileInfo == null || isTextRecognitionLoading}
                        color="primary"
                >
                    Recognize text
                </Button>
                <Button className='image--search--element'
                        onClick={handleLabelRecognition}
                        variant="contained"
                        disabled={currentImage == null || currentImage.fileInfo == null || isLabelRecognitionLoading}
                        color="primary"
                >
                    Recognize labels
                </Button>
                <Button className='image--search--element'
                        onClick={handleRemoveImage}
                        disabled={currentImage == null || currentImage.fileInfo == null || isLoading}
                        variant="contained"
                        color="secondary"
                >
                    Remove Current Image
                </Button>
            </div>
            <div className='image--display'>
                {
                    isLoading ? (
                        <div>Fetching image...</div>
                    ) : (
                        currentImage != null && currentImage.fileInfo != null ? (
                            <img className='image'
                                 src={getFileUrl(currentImage.fileInfo.bucketName, currentImage.fileInfo.objectKey)}
                                 alt='Resource not found :('/>
                        ) : (
                            <div>Please select image</div>
                        )
                    )
                }
            </div>
            {
                isTextRecognitionLoading ? (
                    <div>Detecting text...</div>
                ) : (
                    detectedTextResponse && (
                        <React.Fragment>
                            <div><h3>Detected text:</h3></div>
                            <div className='image--detected--text'>
                                {
                                    detectedTextResponse ? (
                                        detectedTextResponse.map((textResponse, index) => {
                                            return (
                                                <ul key={index}>
                                                    <li>Detected Text: {textResponse.detectedText}</li>
                                                    <li>Confidence: {textResponse.confidence}</li>
                                                </ul>
                                            )
                                        })
                                    ) : false
                                }
                            </div>
                        </React.Fragment>
                    )
                )
            }
            {
                isLabelRecognitionLoading ? (
                    <div>Calculating labels...</div>
                ) : (
                    detectedLabelResponse && (
                        <React.Fragment>
                            <div><h3>Detected labels:</h3></div>
                            <div className='image--detected--text'>
                                {
                                    detectedLabelResponse ? (
                                        detectedLabelResponse.map((labelResponse, index) => {
                                            return (
                                                <ul key={index}>
                                                    <li>Detected label: {labelResponse.name}</li>
                                                    <li>Confidence: {labelResponse.confidence}</li>
                                                </ul>
                                            )
                                        })
                                    ) : false
                                }
                            </div>
                        </React.Fragment>
                    )
                )
            }
        </div>
    );
};

export const ImagesComponent: React.FC<{}> = () => {
    const [currentImageId, setCurrentImageId] = useState<number | null>(null);

    return (
        <div className='images--component'>
            <div className='image--upload--form'>
                <ImagesForm onImageUploaded={image => setCurrentImageId(image)}/>
            </div>
            <div className='image--details'>
                <ImageDetailsComponent currentImageId={currentImageId}
                                       onCurrentImageIdChanged={image => setCurrentImageId(image)}/>
            </div>
        </div>
    )
};
